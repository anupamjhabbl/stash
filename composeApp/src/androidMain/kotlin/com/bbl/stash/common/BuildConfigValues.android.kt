package com.bbl.stash.common

import com.bbl.stash.BuildConfig

actual object BuildConfigValues {
    actual fun getSerpApiKey(): String {
        return BuildConfig.SERP_API_KEY
    }
}