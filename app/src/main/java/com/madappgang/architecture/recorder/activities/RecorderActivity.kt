package com.madappgang.architecture.recorder.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.madappgang.architecture.recorder.R

class RecorderActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recorder)
    }

    fun onClickStop(v: View) {
        Log.d("Stop button", "stop")
    }
}
