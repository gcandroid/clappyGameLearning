package com.kmm.clappygc.util

enum class Platform {
    Android,
    IOS,
    Desktop,
    Web
}

expect fun getPlatform(): Platform