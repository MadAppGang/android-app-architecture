package com.madappgang.architecture.recorder

import android.app.Application
import com.madappgang.architecture.recorder.helpers.FileManager
import com.madappgang.architecture.recorder.helpers.Recorder
import com.madappgang.architecture.recorder.view_state_model.ViewStateStore

/**
 * Created by Bohdan Shchavinskiy <bogdan@madappgang.com> on 12.06.2018.
 */
class AppInstance : Application() {
    companion object {
        val appInstance by lazy { AppInstance() }
    }

    val fileManager by lazy { FileManager() }
    val viewStateStore by lazy { ViewStateStore() }
    val recorder by lazy { Recorder() }
}