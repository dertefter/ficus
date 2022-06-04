package com.dertefter.nstumobile

import android.app.Application
import com.google.android.material.color.DynamicColors

class NSTUMobile: Application() {
    override fun onCreate() {
        super.onCreate()
        DynamicColors.applyToActivitiesIfAvailable(this)
    }
}