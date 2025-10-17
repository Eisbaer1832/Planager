package com.capputinodevelopment.planager

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform