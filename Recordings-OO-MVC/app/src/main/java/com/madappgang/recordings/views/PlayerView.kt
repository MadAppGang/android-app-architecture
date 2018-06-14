/*
 * Copyright 2018 MadAppGang.
 *
 * Created by Andrii Fedorov afedorov@madappgang.com on 6/13/18.
 */

package com.madappgang.recordings.views

import android.content.Context
import android.support.v7.widget.AppCompatButton
import android.util.AttributeSet
import android.widget.FrameLayout
import android.widget.SeekBar
import android.widget.TextView
import com.madappgang.recordings.R
import com.madappgang.recordings.extensions.formatMilliseconds
import com.madappgang.recordings.kit.Player

internal class PlayerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : FrameLayout(context, attrs, defStyleAttr, defStyleRes) {

    var onStartPausePlayer: () -> Unit = {}
    var onProgressChanged: (Int) -> Unit = {}

    private val playerPosition by lazy { findViewById<TextView>(R.id.playerPosition) }
    private val trackDuration by lazy { findViewById<TextView>(R.id.trackDuration) }
    private val startPausePlayer by lazy { findViewById<AppCompatButton>(R.id.startPausePlayer) }
    private val playerSeekBar by lazy { findViewById<SeekBar>(R.id.playerProgress) }

    private var state: Player.State = Player.State.NOT_STARTED

    init {
        inflate(getContext(), R.layout.view_player, this)

        init()
        setState(Player.State.NOT_STARTED)
        setTrackDuration(0)
        setCurrentPosition(0)
    }

    fun setState(state: Player.State) {
        this.state = state
        updateStartPauseButton()
    }

    fun setTrackDuration(duration: Int) {
        playerSeekBar.max = duration
        trackDuration.text = trackDuration.formatMilliseconds(duration)
    }

    fun setCurrentPosition(position: Int) {
        playerPosition.text = playerPosition.formatMilliseconds(position)
        playerSeekBar.progress = position
    }

    private fun init() {
        startPausePlayer.setOnClickListener { onStartPausePlayer.invoke() }

        playerSeekBar.setOnTouchListener { v, event ->
            return@setOnTouchListener state == Player.State.NOT_STARTED
        }

        playerSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(
                seekBar: SeekBar?,
                progress: Int,
                fromUser: Boolean
            ) {
                if (fromUser) {
                    onProgressChanged.invoke(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })
    }

    private fun updateStartPauseButton() {
        startPausePlayer.isEnabled =
            state == Player.State.PLAYING ||
            state == Player.State.PAUSED ||
            state == Player.State.COMPLETED ||
            state == Player.State.NOT_STARTED

        startPausePlayer.text = if (state == Player.State.NOT_STARTED ||
            state == Player.State.PAUSED ||
            state == Player.State.COMPLETED
        ) {
            context.getString(R.string.PlayerActivity_start)
        } else {
            context.getString(R.string.PlayerActivity_pause)
        }
    }
}
