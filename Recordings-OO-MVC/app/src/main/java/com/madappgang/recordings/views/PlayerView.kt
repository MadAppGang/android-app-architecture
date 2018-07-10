/*
 * Copyright 2018 MadAppGang.
 *
 * Created by Andrii Fedorov afedorov@madappgang.com on 6/13/18.
 */

package com.madappgang.recordings.views

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import android.widget.SeekBar
import com.madappgang.recordings.R
import com.madappgang.recordings.formatters.TimeFormatter
import com.madappgang.recordings.kit.Player
import kotlinx.android.synthetic.main.view_player.view.*

internal class PlayerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : FrameLayout(context, attrs, defStyleAttr, defStyleRes) {

    var onStartPausePlayer: () -> Unit = {}
    var onProgressChanged: (Int) -> Unit = {}

    private var isAllowTouch = false

    init {
        inflate(getContext(), R.layout.view_player, this)

        initListener()
        setState(Player.State.NOT_STARTED)
        setTrackDuration(0)
        setCurrentPosition(0)
    }

    fun setState(state: Player.State) {
        isAllowTouch = state != Player.State.NOT_STARTED
        updateStartPauseButton(state)
    }

    fun setTrackDuration(duration: Int) {
        playerProgress.max = duration
        trackDuration.text = TimeFormatter().formatMilliseconds(duration)
    }

    fun setCurrentPosition(position: Int) {
        playerPosition.text = TimeFormatter().formatMilliseconds(position)
        playerProgress.progress = position
    }

    private fun initListener() {
        startPausePlayer.setOnClickListener { onStartPausePlayer.invoke() }

        playerProgress.setOnTouchListener { v, event ->
            return@setOnTouchListener !isAllowTouch
        }

        playerProgress.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
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

    private fun updateStartPauseButton(state: Player.State) {
        startPausePlayer.isEnabled = state != Player.State.PREPARING

        val isShowStart = state == Player.State.NOT_STARTED ||
            state == Player.State.PAUSED ||
            state == Player.State.COMPLETED

        if (isShowStart) {
            startPausePlayer.text = context.getString(R.string.PlayerActivity_start)
        } else {
            startPausePlayer.text = context.getString(R.string.PlayerActivity_pause)
        }
    }
}
