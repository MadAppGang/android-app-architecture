package com.madappgang.madappgangmvvmtestarch.utils

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent
import kotlinx.coroutines.experimental.Job

/**
 * Created by Serhii Chaban sc@madappgang.com on 30.05.18.
 */
class AndroidJob(lifecycle: Lifecycle) : Job by Job(), LifecycleObserver {

    init {
        lifecycle.addObserver(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun destroy() = cancel()
}
