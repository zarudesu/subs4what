package com.subs4what.app

import android.app.Application
import com.google.firebase.FirebaseApp

class SubsApp : Application() {

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
}
