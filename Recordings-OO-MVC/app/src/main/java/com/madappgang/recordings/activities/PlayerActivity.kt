/*
 * Copyright 2018 MadAppGang.
 *
 * Created by Andrii Fedorov afedorov@madappgang.com on 6/7/18.
 */

package com.madappgang.recordings.activities

import android.arch.lifecycle.Observer
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
import com.madappgang.recordings.kit.Player
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

    private var updateUiJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        initToolbar()
        initPlayerProgressBar()
        initStartResumeButton()

        player.state.observe(this, Observer<Player.State> {
            playerSeekBar.max = player.getDuration()
            updateRefreshLayout()
            updatePlayerPosition()
            trackDuration.text = trackDuration.formatMilliseconds(player.getDuration())
            updatePlayerSeekBar()
            updateStartPauseButton()
            if (it == Player.State.PLAYING) {
                updateUiJob = createUpdateUiJob()
            } else {
                updateUiJob?.cancel()
            }
        })
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
        player.stop()
        player.clearData()
    }

    private fun initToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbarTitle.text = track.name
    }

    private fun updatePlayerPosition() {
        playerPosition.text = playerPosition.formatMilliseconds(player.getCurrentPosition())
    }

    private fun updateRefreshLayout() {
        if (player.state.value == Player.State.PREPARING) {
            progressBar.makeVisible()
        } else {
            progressBar.makeGone()
        }
    }

    private fun updatePlayerSeekBar() {
        playerSeekBar.progress = player.getCurrentPosition()
    }

    private fun updateStartPauseButton() {
        val state = player.state.value
        startPausePlayer.isEnabled = state != Player.State.PREPARING

        startPausePlayer.text = if (state == Player.State.NOT_STARTED ||
            state == Player.State.PAUSED ||
            state == Player.State.COMPLETED ||
            state == Player.State.STOPPED
        ) {
            getString(R.string.PlayerActivity_start)
        } else {
            getString(R.string.PlayerActivity_pause)
        }
    }

    private fun initStartResumeButton() {
        startPausePlayer.setOnClickListener {
            when (player.state.value) {
                Player.State.NOT_STARTED -> player.play(track)
                Player.State.PLAYING -> player.pause()
                else -> player.start()
            }
        }
    }

    private fun initPlayerProgressBar() {
        playerSeekBar.setOnTouchListener { v, event ->
            return@setOnTouchListener player.state == Player.State.NOT_STARTED
        }

        playerSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    player.seekTo(progress)
                    updatePlayerSeekBar()
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })
    }

    private fun createUpdateUiJob() = launch(uiContext) {
        while (true) {
            updatePlayerSeekBar()
            updatePlayerPosition()
            delay(300, TimeUnit.MILLISECONDS)
        }
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