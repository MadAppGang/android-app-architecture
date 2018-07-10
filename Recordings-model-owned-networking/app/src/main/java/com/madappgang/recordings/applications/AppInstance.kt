/*
 * Copyright 2018 MadAppGang.
 *
 * Created by Andrii Fedorov afedorov@madappgang.com on 7/9/18.
 */

package com.madappgang.recordings.applications

import android.app.Application

internal class AppInstance : Application() {

    lateinit var dependencyContainer :  DependencyContainer

    override fun onCreate() {
        super.onCreate()
        val configurator = DependencyContainer.Configurator(cacheDir)

        dependencyContainer = DependencyContainer.newInstance(configurator)
    }
}

internal val Application.app: AppInstance
    get() = this as AppInstance