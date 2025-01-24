package com.kmm.clappygc.di

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.Settings
import com.russhwolf.settings.observable.makeObservable
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.module


//
// Created by Code For Android on 21/01/25.
// Copyright (c) 2025 CFA. All rights reserved.
//

@OptIn(ExperimentalSettingsApi::class)
val sharedModule = module {
    single<ObservableSettings> { Settings().makeObservable() }
}

expect val targetModule : Module

fun initializeKoin(
    config: (KoinApplication.() -> Unit)? = null
) {
    startKoin {
        config?.invoke(this)
        modules(sharedModule, targetModule)
    }
}