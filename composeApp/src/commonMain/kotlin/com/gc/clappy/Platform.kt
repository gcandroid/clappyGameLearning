package com.gc.clappy

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform