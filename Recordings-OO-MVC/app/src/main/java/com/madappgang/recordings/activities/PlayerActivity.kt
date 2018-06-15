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
import com.madappgang.recordings.views.PlayerView
import kotlinx.android.synthetic.main.activity_player.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import java.util.concurrent.TimeUnit

internal class PlayerActivity : AppCompatActivity() {

    private val player by lazy { App.dependencyContainer.player }

    private var updateUiJob: Job? = null
    private val uiContext by lazy { UI }

    private val track by lazy { intent.getParcelableExtra(TRACK_KEY) as Track }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        initToolbar()
        updateUi()

        playerView.onStartPausePlayer = { startPausePlayer() }

        playerView.onProgressChanged = {
            player.seekTo(it)
            playerView.setCurrentPosition(player.getCurrentPosition())
        }

        player.state.observe(this, Observer<Player.State> {
            updateUi()
            updateRefreshLayout()
            updateUiJob()
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
        if (isFinishing) {
            player.stop()
            player.release()
        }
    }

    private fun initToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbarTitle.text = track.name
    }

    private fun startPausePlayer() {
        when (player.state.value) {
            Player.State.NOT_STARTED -> player.play(track)
            Player.State.PLAYING -> player.pause()
            else -> player.start()
        }
    }

    private fun updateUiJob() {
        updateUiJob?.cancel()
        if (player.state.value == Player.State.PLAYING) {
            updateUiJob = createUpdateUiJob()
        }
    }

    private fun createUpdateUiJob() = launch(uiContext) {
        while (true) {
            delay(300, TimeUnit.MILLISECONDS)
            updateUi()
        }
    }

    private fun updateRefreshLayout() {
        if (player.state.value == Player.State.PREPARING) {
            progressBar.makeVisible()
        } else {
            progressBar.makeGone()
        }
    }

    private fun updateUi() {
        player.state.value?.let { playerView.setState(it) }
        playerView.setTrackDuration(player.getDuration())
        playerView.setCurrentPosition(player.getCurrentPosition())
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