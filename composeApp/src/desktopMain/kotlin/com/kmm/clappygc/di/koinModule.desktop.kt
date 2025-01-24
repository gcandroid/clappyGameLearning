package com.kmm.clappygc.di

import com.kmm.clappygc.domain.AudioPlayer
import org.koin.core.module.Module
import org.koin.dsl.module

actual val targetModule = module {
    single<AudioPlayer> { AudioPlayer() }
}