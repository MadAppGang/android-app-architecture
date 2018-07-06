package com.madappgang.architecture.recorder.application

import android.app.Application
import com.madappgang.architecture.recorder.managers.ManagersImpl
import com.madappgang.architecture.recorder.managers.ManagersInstance

/**
 * Created by Bohdan Shchavinskiy <bogdan@madappgang.com> on 12.06.2018.
 */
class AppInstance : Application() {
    companion object {
        val managersInstance by lazy { ManagersInstance(ManagersImpl()) }
    }
}