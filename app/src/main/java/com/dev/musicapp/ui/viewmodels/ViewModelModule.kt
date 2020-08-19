package com.dev.musicapp.ui.viewmodels
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


val viewModelModule = module {
    single { MainViewModel(get(), get()) }
    viewModel { SongDetailViewModel(get(), get()) }
    viewModel { PlaylistViewModel(get()) }
    viewModel { ArtistViewModel(get()) }
    viewModel { FavoriteViewModel(get()) }
    viewModel { AlbumViewModel(get()) }
    viewModel { SearchViewModel(get(), get(), get()) }
    viewModel { SongViewModel(get()) }
    viewModel { FolderViewModel(get()) }
}