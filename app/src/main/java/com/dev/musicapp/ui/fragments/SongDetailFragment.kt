package com.dev.musicapp.ui.fragments


import android.os.Bundle
import android.support.v4.media.session.PlaybackStateCompat
import android.view.*
import com.dev.musicapp.R
import com.dev.musicapp.databinding.FragmentSongDetailBinding
import com.dev.musicapp.extensions.*

import com.dev.musicapp.models.MediaItemData
import com.dev.musicapp.ui.viewmodels.SongViewModel
import com.quanlv.musicplayer.ui.fragments.base.BaseSongDetailFragment

import com.dev.musicapp.utils.AutoClearBinding
import com.dev.musicapp.utils.BeatConstants.BIND_STATE_BOUND
import com.dev.musicapp.utils.GeneralUtils
import com.dev.musicapp.utils.GeneralUtils.getSongUri
import kotlinx.android.synthetic.main.fragment_song_detail.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import timber.log.Timber
import kotlin.math.absoluteValue

class SongDetailFragment : BaseSongDetailFragment() {

    private var binding by AutoClearBinding<FragmentSongDetailBinding>(this)
    private val songViewModel by sharedViewModel<SongViewModel>()
    private lateinit var gestureDetector: GestureDetector
    private val minFlingVelocity = 800
    
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = inflater.inflateWithBinding(R.layout.fragment_song_detail, container)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        init()
        initSwipeGestures()
    }

    private fun init() {
        initViewComponents()

        songDetailViewModel.currentData.observe(this) {
            initNeeded(songViewModel.getSongById(it.id), emptyList(), 0L)
            launch {
                val raw = withContext(IO) {
                    GeneralUtils.audio2Raw(context!!, getSongUri(it.id)) ?: byteArrayOf()
                }
                songDetailViewModel.update(raw)
            }
        }

        songDetailViewModel.time.observe(this) {
            val total = songDetailViewModel.currentData.value?.duration ?: 0
            binding.seekBar.apply {
                val t = progress.percentToMs(total).fixToStep(1000)
                if (t != it) {
                    progress = it.fixToPercent(total).fixPercentBounds()
                }
            }
        }

        binding.apply{
            sharedSong.setOnClickListener { shareItem() }
            songTitle.isSelected = true
        }

        songDetailViewModel.currentState.observe(this) {
            songDetailViewModel.update(it.position)
            if (it.state == PlaybackStateCompat.STATE_PLAYING) {
                songDetailViewModel.update(BIND_STATE_BOUND)
            } else songDetailViewModel.update()
        }

        binding.let {
            it.viewModel = songDetailViewModel
            it.lifecycleOwner = this
            it.executePendingBindings()
        }
    }

    override fun onDetach() {
        super.onDetach()
        songDetailViewModel.update(byteArrayOf())
        songDetailViewModel.update()
    }

    private fun initViewComponents() {
        binding.apply {
            nextBtn.setOnClickListener {
                mainViewModel.transportControls()?.skipToNext()
            }
            previousBtn.setOnClickListener {
                mainViewModel.transportControls()?.skipToPrevious()
            }
            seekBar.apply {
                onStartTracking = {
                    songDetailViewModel.update()
                }
                onStopTracking = {
                    val mediaItemData = songDetailViewModel.currentData.value ?: MediaItemData()
                    mainViewModel.transportControls()
                        ?.seekTo((it * mediaItemData.duration / 100).toLong())
                }
                onProgressChanged = { position, byUser ->
                    if (byUser) {
                        val mediaItemData = songDetailViewModel.currentData.value ?: MediaItemData()
                        songDetailViewModel.update((position * mediaItemData.duration / 100).toInt())
                    }
                }
            }
        }
    }
    
    private var touchListener: View.OnTouchListener = View.OnTouchListener {
            v: View, motionEvent: MotionEvent -> gestureDetector.onTouchEvent(motionEvent)
        when (motionEvent.action) {
            MotionEvent.ACTION_DOWN -> {
            }
            MotionEvent.ACTION_UP -> v.performClick()
            else -> {
            }
        }
        true
    }
    
    private fun initSwipeGestures() {
        gestureDetector =
            GestureDetector(activity, object : GestureDetector.OnGestureListener {
                override fun onDown(event: MotionEvent): Boolean {
                    return true
                }

                override fun onFling(
                    e1: MotionEvent,
                    e2: MotionEvent,
                    velocityX: Float,
                    velocityY: Float
                ): Boolean {
                    if (velocityX.absoluteValue > minFlingVelocity) {
                        if (velocityX < 0) {
                            mainViewModel.transportControls()?.skipToNext()
                        } else {
                            mainViewModel.transportControls()?.skipToPrevious()
                        }
                    }
                    return true
                }

                override fun onShowPress(e: MotionEvent?) {
                    Timber.e("onShowPress detected")
                }

                override fun onSingleTapUp(e: MotionEvent?): Boolean {
                    return true
                }

                override fun onLongPress(e: MotionEvent?) {
                    Timber.e("onLongPress detected")
                }

                override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean {
                    return true
                }
            })
        now_playing_cover.setOnTouchListener(touchListener)
    }
}
