package com.madappgang.architecture.recorder.ui.player_page


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
import com.madappgang.architecture.recorder.ui.recorder_page.RecorderActivity
import kotlinx.android.synthetic.main.activity_player.*


class PlayerActivity : AppCompatActivity(), SeekBar.OnSeekBarChangeListener, TextWatcher {

    private var playerViewBinder = PlayerViewBinder(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)
        setSupportActionBar(playerToolbar)

        val path = intent.getStringExtra(FILE_PATH)
        prepareViewBinder()
        playerViewBinder.init(path)

        editRecordName.addTextChangedListener(this)
        seekBar.setOnSeekBarChangeListener(this)
    }

    private fun prepareViewBinder() {
        playerViewBinder.setLabelText = { setLabelText(it) }
        playerViewBinder.setPlayButtonText = { setPlayButtonText(it) }
        playerViewBinder.setPosition = { setPosition(it) }
        playerViewBinder.setRecordDuration = { setDuration(it) }
    }

    private fun setLabelText(fileName: String) {
        playerLabel.text = fileName
        editRecordName.setText(fileName)
    }

    private fun setPlayButtonText(textId: Int) {
        val text = getString(textId)
        playButton.text = text
    }

    private fun setDuration(duration: Int) {
        seekBar.max = duration
        this.duration.text = RecorderActivity.getTimeFormat(duration.toLong())
    }

    override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
        Log.d("seek", "onProgressChanged")
    }

    override fun onStartTrackingTouch(p0: SeekBar?) {
        Log.d("seek", "onStartTrackingTouch")
    }

    override fun onStopTrackingTouch(p0: SeekBar?) {
        Log.d("seek", "onStopTrackingTouch")
        playerViewBinder.playerSeekTo(seekBar.progress)
    }

    override fun afterTextChanged(p0: Editable?) {
        val newName = editRecordName.text.toString()
        if (playerViewBinder.fileName != newName) {
            playerLabel.text = newName
            playerViewBinder.fileName = newName
        }
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

    fun onClickPlay(v: View) {
        playerViewBinder.onClickPlay()
    }

    override fun onPause() {
        playerViewBinder.onPause()
        super.onPause()
    }

    private fun setPosition(progress: Int) {
        seekBar.progress = progress
        currentTime.text = RecorderActivity.getTimeFormat(progress.toLong())
    }

    companion object {
        val FILE_PATH = "file_path"

        fun start(context: Context, filePath: String) {
            val intent = Intent(context, PlayerActivity::class.java)
            intent.putExtra(FILE_PATH, filePath)
            context.startActivity(intent)
        }
    }
}
