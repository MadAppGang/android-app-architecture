/*
 * Copyright 2018 MadAppGang.
 *
 * Created by Andrii Fedorov afedorov@madappgang.com on 6/7/18.
 */

package com.madappgang.recordings

import android.app.Application

class App : Application() {

    companion object {

        val dependencyContainer by lazy { DependencyContainer() }

    }
}