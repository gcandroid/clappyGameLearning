package com.kmm.clappygc

import androidx.compose.ui.window.ComposeUIViewController
import com.kmm.clappygc.di.initializeKoin

fun MainViewController() = ComposeUIViewController(
    configure = { initializeKoin() }
) { App() }