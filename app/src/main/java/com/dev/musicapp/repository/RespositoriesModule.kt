

package com.dev.musicapp.repository

import org.koin.dsl.bind
import org.koin.dsl.module

val repositoriesModule = module {
    factory { PlaylistRepositoryImplementation(get()) } bind PlaylistRepository::class
    factory { SongsRepositoryImplementation(get()) } bind SongsRepository::class
    factory { FavoritesRepositoryImplementation(get()) } bind FavoritesRepository::class
    factory { FoldersRepositoryImplementation(get(), get()) } bind FoldersRepository::class
    factory { AlbumsRepositoryImplementation(get()) } bind AlbumsRepository::class
    factory { ArtistsRepositoryImplementation(get()) } bind ArtistsRepository::class
}