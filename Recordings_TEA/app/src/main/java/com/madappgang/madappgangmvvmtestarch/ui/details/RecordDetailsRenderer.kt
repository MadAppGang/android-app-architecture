package com.madappgang.madappgangmvvmtestarch.ui.details

import com.madappgang.madappgangmvvmtestarch.R
import net.semanticer.renderit.BaseStateRenderer

/**
 * Created by Serhii Chaban sc@madappgang.com on 09.07.18.
 */
class RecordDetailsRenderer(view: RecordDetailsView?) : BaseStateRenderer<RecordDetailsView, RecordDetailsState>(view) {
    override fun RecordDetailsView.render(state: RecordDetailsState) {
        val playerState = state.playerState
        state from { it.fileName } into {
            title().text = it
        } from { it.progress } into {
            seekbar().progress = it
        } from { it.duration } into {
            seekbar().max = it
        } from { it.playerState } into {
            playPause().setImageResource(when (playerState) {
                PlayerState.play -> R.drawable.ic_play_circle_filled_black_24dp
                PlayerState.pause -> R.drawable.ic_pause_circle_filled_black_24dp
            })
        }

    }

}