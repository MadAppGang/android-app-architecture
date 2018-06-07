package com.madappgang.recordings.app

import android.app.Application

/**
 * Created by Andrii Fedorov afedorov@madappgang.com on 6/7/18.
 */

class App: Application() {
    companion object {
        val dependencyContainer by lazy { DependencyContainer() }
    }

}