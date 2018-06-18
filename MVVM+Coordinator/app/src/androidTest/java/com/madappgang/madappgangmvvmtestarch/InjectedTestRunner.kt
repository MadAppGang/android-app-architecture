package com.madappgang.madappgangmvvmtestarch

/**
 * Created by Serhii Chaban sc@madappgang.com on 18.06.18.
 */
import android.app.Application
import android.os.Bundle
import com.madappgang.madappgangmvvmtestarch.application.asApp

class InjectedTestRunner : android.support.test.runner.AndroidJUnitRunner() {

    override fun callApplicationOnCreate(app: Application) {
        app.asApp().kodein.mutable = true
        super.callApplicationOnCreate(app)
    }
}