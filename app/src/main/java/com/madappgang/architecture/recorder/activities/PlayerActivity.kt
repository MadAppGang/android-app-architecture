package com.madappgang.architecture.recorder.activities


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.SeekBar
import com.madappgang.architecture.recorder.R
import com.madappgang.architecture.recorder.helpers.FileManager
import com.madappgang.architecture.recorder.helpers.FileManager.Companion.recordFormat
import com.madappgang.architecture.recorder.helpers.Player
import kotlinx.android.synthetic.main.activity_player.*


class PlayerActivity : AppCompatActivity(), SeekBar.OnSeekBarChangeListener, TextWatcher, Player.PlayerCallback {

    private lateinit var player: Player
    private var isClickOnButton = false
    private val handler = Handler()
    private lateinit var filePath: String
    private lateinit var fileName: String
    private lateinit var originalName: String
    private val fileManager = FileManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)
        setSupportActionBar(toolbar)

        val filePath = intent.getStringExtra(FILE_PATH)
        originalName = filePath.split("/").last().removeSuffix(recordFormat)
        fileName = originalName
        this.filePath = filePath.substring(0, filePath.length - (fileName.length + recordFormat.length + 1))
        player = Player(filePath, this)
        label.text = fileName
        editRecordName.setText(fileName)
        editRecordName.addTextChangedListener(this)
        seekBar.setOnSeekBarChangeListener(this)
    }

    override fun setDuration(duration: Int) {
        seekBar.max = duration
        this.duration.text = RecorderActivity.getTimeFormat(duration.toLong())
    }

    override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
        Log.d("seek", "onProgressChanged")
    }

    private fun seekChange() {
        val progress = seekBar.progress
        player.seekTo(progress)
        currentTime.text = RecorderActivity.getTimeFormat(progress.toLong())
    }

    private fun startPlayProgressUpdater() {
        if (player.isPlaying()) {
            isClickOnButton = false
            val curPos = player.getCurrentPosition()
            seekBar.progress = curPos
            currentTime.text = RecorderActivity.getTimeFormat(curPos.toLong())
            val notification = Runnable { startPlayProgressUpdater() }
            handler.postDelayed(notification, 10)
        } else if (!isClickOnButton) {
            player.pause()
            playButton.text = getString(R.string.player_button_play)
            seekBar.progress = 0
            currentTime.text = RecorderActivity.getTimeFormat(0L)
        }
    }

    override fun onStartTrackingTouch(p0: SeekBar?) {
        Log.d("seek", "onStartTrackingTouch")
    }

    override fun onStopTrackingTouch(p0: SeekBar?) {
        Log.d("seek", "onStopTrackingTouch")
        seekChange()
    }

    override fun afterTextChanged(p0: Editable?) {
        val newName = editRecordName.text.toString()
        label.text = newName
        fileName = newName
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

    private fun renameFile() {
        fileManager.renameFile(filePath, originalName, fileName)
    }

    fun onClickPlay(v: View) {
        isClickOnButton = true
        Log.d("Play button", "play")
        if (player.isPlaying()) {
            player.pause()
            playButton.text = getString(R.string.player_button_resume_play)
        } else {
            player.play()
            playButton.text = getString(R.string.player_button_pause)
            startPlayProgressUpdater()
        }
    }

    override fun onPause() {
        if (originalName != fileName) renameFile()
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        player.destroy()
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
