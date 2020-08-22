package com.dev.musicapp.ui.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.media.session.PlaybackStateCompat.*
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import com.dev.musicapp.R
import com.dev.musicapp.extensions.*
import com.dev.musicapp.ui.activities.base.BaseActivity
import com.dev.musicapp.models.PlaybackState
import com.dev.musicapp.models.Song
import com.dev.musicapp.ui.fragments.*
import com.dev.musicapp.ui.viewmodels.*
import com.dev.musicapp.utils.BeatConstants
import com.dev.musicapp.utils.BeatConstants.BIND_STATE_BOUND
import com.dev.musicapp.utils.BeatConstants.FAVORITE_ID
import com.dev.musicapp.utils.BeatConstants.NOW_PLAYING
import com.dev.musicapp.utils.BeatConstants.PLAY_LIST_DETAIL
import com.dev.musicapp.utils.GeneralUtils.getStoragePaths
import com.dev.musicapp.utils.SettingsUtility
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_SHORT
import com.google.gson.Gson
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber


class MainActivity : BaseActivity() {

    private val playListViewModel by inject<PlaylistViewModel>()
    private val favoriteViewModel by inject<FavoriteViewModel>()
    private val songViewModel by inject<SongViewModel>()
    private val songDetailViewModel by inject<SongDetailViewModel>()
    private val settingsUtility by inject<SettingsUtility>()

    private val viewModel by viewModel<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init(savedInstanceState)
        requestPermissions()

    }

    private fun requestPermissions(){
        val permissions = arrayOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WAKE_LOCK,
            Manifest.permission.INTERNET)
        ActivityCompat.requestPermissions(this,permissions,0)
    }

    override fun onStop() {
        super.onStop()
        settingsUtility.didStop = true
    }

    private fun init(savedInstanceState: Bundle?) {
        viewModel.binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        if (savedInstanceState == null) {
            replaceFragment(
                R.id.nav_host_fragment,
                LibraryFragment(),
                BeatConstants.LIBRARY
            )
        }
        if (didPermissionsGrant()) finishCreatingView()
    }

    private fun finishCreatingView(){
        songDetailViewModel.time.observe(this) {
            val total = songDetailViewModel.currentData.value?.duration ?: 0
            viewModel.binding.progressCircular.apply {
                val t = progress.percentToMs(total).fixToStep(1000)
                if (t != it) {
                    progress = it.fixToPercent(total).fixPercentBounds()
                }
            }
        }

        songDetailViewModel.currentState.observe(this) {
            songDetailViewModel.update(it.position)
            if (it.state == STATE_PLAYING) {
                songDetailViewModel.update(BIND_STATE_BOUND)
            } else songDetailViewModel.update()
        }

        viewModel.binding.let {
            it.viewModel = songDetailViewModel
            it.executePendingBindings()

            it.title.isSelected = true

            it.lifecycleOwner = this
        }

        songDetailViewModel.currentData
            .observe(this) {
                val fragment = supportFragmentManager.findFragmentByTag(NOW_PLAYING)
                if (it.id !in arrayOf(0L, -1L)) {
                    if (fragment == null) {
                        viewModel.showMiniPlayer()
                    } else viewModel.hideMiniPlayer()
                } else viewModel.hideMiniPlayer()
            }
        handlePlaybackIntent(intent)
    }

    fun onSongLyricClick(v: View) {
        addFragment(
            R.id.nav_host_fragment,
            LyricFragment(),
            BeatConstants.LYRIC,
            true
        )
    }

    fun onSongInfoClick(v: View) {
        addFragment(
            R.id.nav_host_fragment,
            SongDetailFragment(),
            NOW_PLAYING,
            true
        )
    }

    override fun onPermissionsGrantResult(result: Boolean) {
        super.onPermissionsGrantResult(result)
        if(result){
            replaceFragment(
                R.id.nav_host_fragment,
                LibraryFragment(),
                BeatConstants.LIBRARY
            )
            finishCreatingView()
        }
    }

    override fun onBackPressed() {
        var isDismiss = true
        supportFragmentManager.fragments.forEach {
            isDismiss = when (it) {
                is AlbumDetailFragment -> it.onBackPressed()
                is ArtistDetailFragment -> it.onBackPressed()
                is PlaylistDetailFragment -> it.onBackPressed()
                is FolderDetailFragment -> it.onBackPressed()
                else -> true
            }
        }
        if (isDismiss) super.onBackPressed()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        data ?: return
        data.extras ?: return
        val name = data.extras!!.getString(PLAY_LIST_DETAIL)
        val songs = Gson().fromJson<List<Song>>(data.extras?.getString("SONGS")!!)
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_CANCELED) {
                createPlayList(name, songs)
            } else {
                createPlayList(name, songs, true)
            }
        }
    }

    private fun createPlayList(
        name: String?,
        selectedSong: List<Song>,
        showOnEnd: Boolean = false
    ) {
        val id = playListViewModel.create(name!!, selectedSong)
        if (id > 0) {
            if (!showOnEnd) {
                viewModel.binding.mainContainer.snackbar(
                    SUCCESS,
                    getString(R.string.playlist_added_success),
                    LENGTH_SHORT
                )
                return
            }
            addFragment(
                R.id.nav_host_fragment,
                PlaylistDetailFragment(),
                PLAY_LIST_DETAIL,
                extras = bundleOf(PLAY_LIST_DETAIL to id)
            )
        } else {
            viewModel.binding.mainContainer.snackbar(
                ERROR,
                getString(R.string.playlist_added_error),
                LENGTH_SHORT
            )
        }
    }

    fun playPauseClick(view: View) {
        val mediaItemData = songDetailViewModel.currentState.value ?: PlaybackState()
        when (mediaItemData.state) {
            STATE_PLAYING -> viewModel.transportControls()?.pause()
            else -> viewModel.transportControls()?.play()
        }
    }

    fun shuffleModeClick(view: View) {
        val mediaItemData = songDetailViewModel.currentState.value ?: PlaybackState()
        when (mediaItemData.shuffleMode) {
            SHUFFLE_MODE_ALL -> viewModel.transportControls()?.setShuffleMode(SHUFFLE_MODE_NONE)
            else -> viewModel.transportControls()?.setShuffleMode(SHUFFLE_MODE_ALL)
        }
    }

    fun repeatModeClick(view: View) {
        val mediaItemData = songDetailViewModel.currentState.value ?: PlaybackState()
        when (mediaItemData.repeatMode) {
            REPEAT_MODE_ONE -> viewModel.transportControls()?.setRepeatMode(REPEAT_MODE_ALL)
            REPEAT_MODE_ALL -> viewModel.transportControls()?.setRepeatMode(REPEAT_MODE_NONE)
            else -> viewModel.transportControls()?.setRepeatMode(REPEAT_MODE_ONE)
        }
    }

    fun toggleAddToFav(v: View) {
        val mediaItemData = songDetailViewModel.currentData.value ?: return
        val song = songViewModel.getSongById(mediaItemData.id)
        if (!favoriteViewModel.favExist(FAVORITE_ID)) return
        if (favoriteViewModel.songExist(song.id)) {
            val resp = favoriteViewModel.deleteSongByFavorite(FAVORITE_ID, longArrayOf(song.id))
            showSnackBar(v, resp, 0)
        } else {
            val resp = favoriteViewModel.addToFavorite(FAVORITE_ID, listOf(song))
            showSnackBar(v, resp, 1)
        }
    }

    private fun showSnackBar(view: View, resp: Int, type: Int) {
        val ok = when (type) {
            0 -> getString(R.string.song_no_fav_ok)
            else -> getString(R.string.song_fav_ok)
        }

        if (resp > 0) view.snackbar(SUCCESS, ok, LENGTH_SHORT)
    }

    private fun handlePlaybackIntent(intent: Intent?) {
        intent?.action ?: return

        when (intent.action!!) {
            Intent.ACTION_VIEW -> {
                val path = intent.data?.path ?: return
                Timber.d("path: $path")
                // Some file managers still send a file path instead of media uri,
                // so data needs to be verified.
                val storagePaths = getStoragePaths(this)
                val song = if (path.contains(storagePaths[0])) {
                    // Get song from a path
                    songViewModel.getSongFromPath(path)
                } else {
                    // Get song by id if it is a media uri
                    // The last part of a URI is the id, so just need to get it
                    songViewModel.getSongById(path.split("/").last().toLong())
                }
                viewModel.mediaItemClicked(song.toMediaItem(), null)
            }
        }
    }

    fun didPermissionsGrant() = permissionsGranted
}
