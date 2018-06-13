package com.madappgang.madappgangmvvmtestarch

/**
 * Created by Serhii Chaban sc@madappgang.com on 13.06.18.
 */

import android.app.Application
import android.content.Context
import android.os.Bundle
import android.support.test.runner.AndroidJUnitRunner
import com.madappgang.madappgangmvvmtestarch.application.AppInstance

class InjectedTestRunner : AndroidJUnitRunner() {

    override fun callApplicationOnCreate(app: Application) {
        super.callApplicationOnCreate(app)
    }
}

fun Context.asApp() = this as AppInstance
