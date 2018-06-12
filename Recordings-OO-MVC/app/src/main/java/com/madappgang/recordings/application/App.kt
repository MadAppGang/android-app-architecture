/*
 * Copyright 2018 MadAppGang.
 *
 * Created by Andrii Fedorov afedorov@madappgang.com on 6/7/18.
 */

package com.madappgang.recordings.application

import android.app.Application

class App : Application() {

    companion object {

        lateinit var dependencyContainer : DependencyContainer

    }

    override fun onCreate() {
        super.onCreate()
        dependencyContainer = DependencyContainer(getExternalFilesDir(null))
    }
}