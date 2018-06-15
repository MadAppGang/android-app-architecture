/*
 * Copyright 2018 MadAppGang.
 *
 * Created by Andrii Fedorov afedorov@madappgang.com on 6/10/18.
 */

package com.madappgang.recordings.extensions

import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.ViewGroup


internal fun AppCompatActivity.showError(throwable: Throwable) {
    val rootView = findViewById<ViewGroup>(android.R.id.content).getChildAt(0)
    val message = throwable.message ?: "Undefined error"
    Snackbar.make(rootView, message, Snackbar.LENGTH_INDEFINITE).show()
}