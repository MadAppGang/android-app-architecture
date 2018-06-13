/*
 * Copyright 2018 MadAppGang.
 *
 * Created by Andrii Fedorov afedorov@madappgang.com on 6/7/18.
 */

package com.madappgang.recordings.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.AppCompatButton
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.widget.ProgressBar
import android.widget.SeekBar
import android.widget.TextView
import com.madappgang.recordings.R
import com.madappgang.recordings.application.App
import com.madappgang.recordings.core.Track
import com.madappgang.recordings.extensions.formatMilliseconds
import com.madappgang.recordings.extensions.makeGone
import com.madappgang.recordings.extensions.makeVisible
import com.madappgang.recordings.media.AudioPlayer
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import java.util.concurrent.TimeUnit

class PlayerActivity : AppCompatActivity() {

    private val track by lazy { intent.getParcelableExtra(TRACK_KEY) as Track }

    private val uiContext by lazy { App.dependencyContainer.uiContext }
    private val bgContext by lazy { App.dependencyContainer.bgContext }
    private val player by lazy { App.dependencyContainer.player }

    private val toolbar by lazy { findViewById<Toolbar>(R.id.toolbar) }
    private val toolbarTitle by lazy { findViewById<TextView>(R.id.toolbarTitle) }
    private val playerPosition by lazy { findViewById<TextView>(R.id.playerPosition) }
    private val trackDuration by lazy { findViewById<TextView>(R.id.trackDuration) }
    private val startPausePlayer by lazy { findViewById<AppCompatButton>(R.id.startPausePlayer) }
    private val playerSeekBar by lazy { findViewById<SeekBar>(R.id.playerProgress) }
    private val progressBar by lazy { findViewById<ProgressBar>(R.id.progressBar) }

    private val audioPlayer by lazy { AudioPlayer() }
    private var playerState = PlayerState.NOT_STARTED
    private var updateUiJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        initPlayerCallback()
        initToolbar()
        initUi()
    }

    private fun initPlayerCallback() {
        player.onPlay = { audioPlayer.prepare(it.absolutePath) }

        audioPlayer.onPrepared = { startAudioPlaying() }

        audioPlayer.onCompleted = {
            playerState = PlayerState.COMPLETED
            updateUiJob?.cancel()
            updateUi()
        }
    }

    private fun initToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbarTitle.text = track.name
    }

    private fun initUi() {
        updateUi()
        initStartPauseButton()
        initPlayerProgressBar()
    }

    private fun updateUi() {
        updateProgressBar()
        updatePlayerTimePositionView()
        updateTrackDurationView()
        updatePlayerSeekBar()
        updateStartPauseButton()
    }

    private fun updateProgressBar() {
        if (playerState == PlayerState.PREPARING) {
            progressBar.makeVisible()
        } else {
            progressBar.makeGone()
        }
    }

    private fun updatePlayerTimePositionView() {
        val position = audioPlayer.getCurrentPosition()
        playerPosition.text = playerPosition.formatMilliseconds(position)
    }

    private fun updateTrackDurationView() {
        val duration = audioPlayer.getDuration()
        trackDuration.text = trackDuration.formatMilliseconds(duration)
    }

    private fun updatePlayerSeekBar() {
        playerSeekBar.progress = if (playerState ==PlayerState.NOT_STARTED ||
            playerState == PlayerState.PREPARING
        ) {
            0
        } else{
            audioPlayer.getCurrentPosition().toInt()
        }
    }

    private fun updateStartPauseButton() {
        startPausePlayer.isEnabled =
            playerState == PlayerState.PLAYING ||
            playerState == PlayerState.PAUSED ||
            playerState == PlayerState.COMPLETED ||
            playerState == PlayerState.NOT_STARTED

        startPausePlayer.text = if (playerState == PlayerState.NOT_STARTED ||
            playerState == PlayerState.PAUSED ||
            playerState == PlayerState.COMPLETED
        ) {
            getString(R.string.PlayerActivity_start)
        } else {
            getString(R.string.PlayerActivity_pause)
        }
    }

    private fun initPlayerProgressBar() {
        playerSeekBar.max = audioPlayer.getDuration().toInt()

        playerSeekBar.setOnTouchListener { v, event ->
            return@setOnTouchListener playerState == PlayerState.NOT_STARTED
        }

        playerSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    audioPlayer.seekTo(progress)
                    updatePlayerTimePositionView()
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })
    }

    private fun initStartPauseButton() {
        updateStartPauseButton()

        startPausePlayer.setOnClickListener {
            when (playerState) {
                PlayerState.NOT_STARTED -> {
                    playerState = PlayerState.PREPARING
                    initPlayerProgressBar()
                    player.play(track)
                }

                PlayerState.PLAYING -> pauseAudioPlaying()
                else -> startAudioPlaying()
            }
            updateUi()
        }
    }

    private fun pauseAudioPlaying() {
        updateUiJob?.cancel()
        playerState = PlayerState.PAUSED
        audioPlayer.pause()
    }

    private fun startAudioPlaying() {
        updateUiJob = createUpdateUiJob()
        audioPlayer.start()
        playerState = PlayerState.PLAYING
        initPlayerProgressBar()
    }

    private fun createUpdateUiJob() = launch(uiContext) {
        while (true) {
            updateUi()
            delay(300, TimeUnit.MILLISECONDS)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        updateUiJob?.cancel()
        if (playerState != PlayerState.NOT_STARTED) {
            audioPlayer.stop()
        }
        player.stop()
    }

    companion object {

        private val TRACK_KEY = "track_key"

        fun start(context: Context, track: Track) {
            val intent = Intent(context, PlayerActivity::class.java)
            intent.putExtra(TRACK_KEY, track)
            context.startActivity(intent)
        }
    }
}

private enum class PlayerState {
    NOT_STARTED,
    PREPARING,
    PLAYING,
    PAUSED,
    COMPLETED
}