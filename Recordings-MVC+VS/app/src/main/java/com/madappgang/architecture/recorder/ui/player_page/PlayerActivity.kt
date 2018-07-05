package com.madappgang.architecture.recorder.ui.player_page


import android.arch.lifecycle.Observer
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
import com.madappgang.architecture.recorder.application.AppInstance
import com.madappgang.architecture.recorder.data.services.PlayerService
import com.madappgang.architecture.recorder.data.storages.FileStorage.Companion.recordFormat
import com.madappgang.architecture.recorder.ui.player_page.PlayerViewState.PlayerState.*
import com.madappgang.architecture.recorder.ui.recorder_page.RecorderActivity
import kotlinx.android.synthetic.main.activity_player.*


class PlayerActivity : AppCompatActivity(), SeekBar.OnSeekBarChangeListener, TextWatcher, PlayerService.PlayerCallback {

    private var isPlayerPause = false
    private val handler = Handler()
    private lateinit var filePath: String
    private lateinit var fileDirectory: String
    private lateinit var fileName: String
    private lateinit var originalName: String
    private var playerService: PlayerService = AppInstance.managersInstance.playerService
    private val fileManager = AppInstance.managersInstance.fileManager
    private val viewStateStore = AppInstance.managersInstance.viewStateStore
    private val playerViewStateStore = viewStateStore.playerViewStateStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)
        setSupportActionBar(playerToolbar)

        val path = intent.getStringExtra(FILE_PATH)
        val resumePlay = currentViewState().originalFilePath.isNotEmpty()
                && currentViewState().filePath.isNotEmpty()
                && path == currentViewState().originalFilePath
        filePath = if (resumePlay) {
            currentViewState().filePath
        } else {
            playerViewStateStore?.updateFilePath(path)
            playerViewStateStore?.updateOriginalFilePath(path)
            playButton.text = getString(R.string.player_button_play)
            playerViewStateStore?.changePlaybackPosition(0)
            path
        }

        originalName = filePath.split("/").last().removeSuffix(recordFormat)
        fileName = originalName
        fileDirectory = filePath.removeSuffix(recordFormat)
        fileDirectory = fileDirectory.substring(0, fileDirectory.length - (fileName.length + 1))
        playerService.init(filePath, this)
        playerLabel.text = fileName
        editRecordName.setText(fileName)
        editRecordName.addTextChangedListener(this)
        seekBar.setOnSeekBarChangeListener(this)

        if (resumePlay && currentViewState().playerState != null && currentViewState().playerState != STOP) {
            seekToPosition(currentViewState().progress.let { it } ?: 0)
            if (currentViewState().playerState == PLAY) {
                startPlaying()
            } else {
                playerService.play()
                playerService.pause()
                playButton.text = getString(R.string.player_button_resume_play)
            }
        }

        playerViewStateStore?.playerViewState?.observe(this, Observer<PlayerViewState> {
            it?.let { handle(it) }
        })
    }

    override fun setDuration(duration: Int) {
        seekBar.max = duration
        this.duration.text = RecorderActivity.getTimeFormat(duration.toLong())
    }

    private fun seekChange() {
        playerViewStateStore?.playerSeekTo(seekBar.progress)
    }

    private fun startPlayProgressUpdater() {
        if (playerService.isPlaying()) {
            playerViewStateStore?.changePlaybackPosition(playerService.getCurrentPosition())
            val notification = Runnable { startPlayProgressUpdater() }
            handler.postDelayed(notification, 10)
        } else if (!isPlayerPause) {
            playerViewStateStore?.updatePlayState(STOP)
        }
    }

    override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
        Log.d("seek", "onProgressChanged")
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
        if (fileName != newName) {
            playerLabel.text = newName
            fileName = newName
        }
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

    private fun renameFile() {
        val newFile = "$fileDirectory/$fileName$recordFormat"
        if (currentViewState().filePath != newFile) {
            if (fileManager.renameFile(currentViewState().filePath, newFile)) {
                playerViewStateStore?.updateFilePath(newFile)
            }
        }
    }

    fun onClickPlay(v: View) {
        if (playerService.isPlaying()) {
            playerViewStateStore?.updatePlayState(PAUSE)
        } else {
            playerViewStateStore?.updatePlayState(PLAY)
        }
    }

    override fun onPause() {
        renameFile()
        playerService.pause()
        isPlayerPause = true
        super.onPause()
    }

    private fun currentViewState(): PlayerViewState = playerViewStateStore?.playerView
            ?: PlayerViewState()

    private fun handle(viewState: PlayerViewState) {
        when (viewState.action) {
            PlayerViewState.Action.CHANGE_PLAYBACK_POSITION -> {
                val progress = viewState.progress.let { it } ?: 0
                seekBar.progress = progress
                currentTime.text = RecorderActivity.getTimeFormat(progress.toLong())
            }
            PlayerViewState.Action.PLAYER_SEEK_TO -> {
                seekToPosition(viewState.progress.let { it } ?: 0)
            }
            PlayerViewState.Action.UPDATE_PLAY_STATE -> {
                when (viewState.playerState) {
                    PLAY -> {
                        startPlaying()
                    }
                    PAUSE -> {
                        playerService.pause()
                        isPlayerPause = true
                        playButton.text = getString(R.string.player_button_resume_play)
                    }
                    STOP -> {
                        playerService.pause()
                        playButton.text = getString(R.string.player_button_play)
                        playerViewStateStore?.changePlaybackPosition(0)
                    }
                }
            }
        }
    }

    private fun startPlaying() {
        playerService.play()
        isPlayerPause = false
        playButton.text = getString(R.string.player_button_pause)
        startPlayProgressUpdater()
    }

    private fun seekToPosition(progress: Int) {
        playerService.seekTo(progress)
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
