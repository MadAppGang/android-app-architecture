package com.madappgang.architecture.recorder.activities


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.SeekBar
import com.madappgang.architecture.recorder.R
import kotlinx.android.synthetic.main.activity_player.*

class PlayerActivity : AppCompatActivity(), SeekBar.OnSeekBarChangeListener, TextWatcher {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        editRecordName.addTextChangedListener(this)
        seekBar.setOnSeekBarChangeListener(this)
    }

    override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
        Log.d("seek", "onProgressChanged")
    }

    override fun onStartTrackingTouch(p0: SeekBar?) {
        Log.d("seek", "onStartTrackingTouch")
    }

    override fun onStopTrackingTouch(p0: SeekBar?) {
        Log.d("seek", "onStopTrackingTouch")
    }

    override fun afterTextChanged(p0: Editable?) {}
    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        Log.d("onTextChanged", p0.toString())
    }

    fun onClickPlay(v: View) {
        Log.d("Play button", "play")
    }

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, PlayerActivity::class.java)
            context.startActivity(intent)
        }
    }
}
