package com.madappgang.madappgangmvvmtestarch.ui.details

import android.arch.lifecycle.LifecycleOwner
import android.support.v7.widget.AppCompatButton
import android.support.v7.widget.AppCompatTextView
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView

/**
 * Created by Serhii Chaban sc@madappgang.com on 09.07.18.
 */
interface RecordDetailsView : LifecycleOwner {
    fun seekbar(): SeekBar
    fun playPause(): ImageView
    fun seekForward(): ImageView
    fun seekBack(): ImageView
    fun title(): TextView

}