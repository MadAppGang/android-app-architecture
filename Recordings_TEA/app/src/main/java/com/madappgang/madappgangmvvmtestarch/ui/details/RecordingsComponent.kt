package com.madappgang.madappgangmvvmtestarch.ui.details


import com.madappgang.madappgangmvvmtestarch.model.models.SourceFile
import com.madappgang.madappgangmvvmtestarch.model.service.PlayerService
import com.madappgang.madappgangmvvmtestarch.model.useCases.GetRecordingUseCase
import com.madappgang.madappgangmvvmtestarch.utils.Result
import cz.inventi.elmdroid.*
import io.reactivex.Observable.interval
import io.reactivex.Single
import java.util.concurrent.TimeUnit

class RecordingsComponent(val configurator: Configurator) : Component<RecordDetailsState, RecordingMsg, PlayerCmd> {
    class Configurator(
            val recordId: String,
            val getRecordingService: GetRecordingUseCase,
            val mediaPlayer: PlayerService
    )

    override fun initState(): RecordDetailsState = RecordDetailsState("", 0, 0, PlayerState.pause)

    override fun update(msg: RecordingMsg, prevState: RecordDetailsState): Pair<RecordDetailsState, PlayerCmd?> = when (msg) {
        Nothing -> prevState to null
        Tick -> updatePlayerProgress(prevState) to null
        PlayClicked -> prevState to if (prevState.playerState == PlayerState.play) PauseAction else PlayAction
        SeekForwardClicked -> prevState.copy(progress = prevState.progress + 10 * 1000) to SeekAction(prevState.progress + 10 * 1000)
        SeekBackwardClicked -> prevState.copy(progress = prevState.progress - 10 * 1000) to SeekAction(prevState.progress - 10 * 1000)
        is SeekSelected -> prevState.copy(progress = msg.seekTo) to SeekAction(msg.seekTo)
        is GetFileSuccess -> prevState.withCmd(InitPlayerAction(msg.source))
        is GetFileError -> prevState.noCmd()
        PlayerPlaying -> prevState.copy(playerState = PlayerState.play).noCmd()
        PlayerPaused -> prevState.copy(playerState = PlayerState.pause).noCmd()
        PlayerStopped -> prevState.copy(playerState = PlayerState.pause).noCmd()
        PlayerError -> prevState.copy(playerState = PlayerState.pause).noCmd()
        PlayerInitEvent -> prevState.withCmd(LoadAction)
        PlayerDeinitEvent -> prevState.withCmd(DeinitPlayerAction)
    }

    private fun updatePlayerProgress(prevState: RecordDetailsState): RecordDetailsState {
        val progress = configurator.mediaPlayer.currentPosition
        val duration = configurator.mediaPlayer.duration
        return prevState.copy(progress = progress, duration = duration)
    }


    override fun call(cmd: PlayerCmd): Single<RecordingMsg> = when (cmd) {
        is SeekAction -> Single.create {
            configurator.mediaPlayer.seekTo(cmd.seekTo)
            Nothing
        }
        PlayAction -> Single.create {
            configurator.mediaPlayer.start()
            Nothing
        }
        PauseAction -> Single.create {
            configurator.mediaPlayer.pause()
            Nothing
        }
        LoadAction -> getRecording(configurator.recordId, configurator.getRecordingService)
        is InitPlayerAction -> Single.create {
            configurator.mediaPlayer.setDataSource(cmd.file.id)
            configurator.mediaPlayer.startPlay()
            Nothing
        }
        DeinitPlayerAction -> Single.create {
            configurator.mediaPlayer.stop()
            configurator.mediaPlayer.reset()
            configurator.mediaPlayer.release()
            Nothing
        }
    }

    override fun subscriptions(): List<Sub<RecordDetailsState, RecordingMsg>> = listOf(CounterSubscription(),
            PlayerStateSubscription(configurator.mediaPlayer))
}


// Tasks
fun getRecording(recordId: String, getRecordingService: GetRecordingUseCase): Single<RecordingMsg> {
    val get = getRecordingService[recordId]
    return Single.just(when (get) {
        is Result.Success -> GetFileSuccess(get.value)
        is Result.Error -> GetFileError(get.error)
    })
}

// Subscriptions
class PlayerStateSubscription(val mediaPlayer: PlayerService) : StatelessSub<RecordDetailsState, RecordingMsg>() {
    override fun invoke(): io.reactivex.Observable<RecordingMsg> = mediaPlayer.playerState.map {
        when (it) {
            is PlayerService.PlayerState.Initial -> Nothing
            is PlayerService.PlayerState.Inited -> Nothing
            is PlayerService.PlayerState.Started -> PlayerPlaying
            is PlayerService.PlayerState.Paused -> PlayerPaused
            is PlayerService.PlayerState.Stopped -> PlayerStopped
            is PlayerService.PlayerState.Reseted -> PlayerStopped
            is PlayerService.PlayerState.Released -> PlayerStopped
            is PlayerService.PlayerState.Throuble.PlayerError -> PlayerError
            is PlayerService.PlayerState.Throuble.Error -> PlayerError
        }
    }
}

class CounterSubscription : StatefulSub<RecordDetailsState, RecordingMsg>() {
    override fun invoke(state: RecordDetailsState): io.reactivex.Observable<RecordingMsg> = interval(1, TimeUnit.SECONDS).map { Tick }
    override fun isDistinct(s1: RecordDetailsState, s2: RecordDetailsState) = s1.duration != s2.duration
}

enum class PlayerState {
    play,
    pause
}

// State
data class RecordDetailsState(
        val fileName: String,
        val progress: Int,
        val duration: Int,
        val playerState: PlayerState
) : State

// Msg
sealed class RecordingMsg : Msg

object PlayerInitEvent : RecordingMsg()
object PlayerDeinitEvent : RecordingMsg()
object Nothing : RecordingMsg()
object Tick : RecordingMsg()
object PlayClicked : RecordingMsg()
object SeekForwardClicked : RecordingMsg()
object SeekBackwardClicked : RecordingMsg()
data class SeekSelected(val seekTo: Int) : RecordingMsg()
data class GetFileSuccess(val source: SourceFile) : RecordingMsg()
data class GetFileError(val error: Throwable) : RecordingMsg()
object PlayerPlaying : RecordingMsg()
object PlayerPaused : RecordingMsg()
object PlayerStopped : RecordingMsg()
object PlayerError : RecordingMsg()

// Cmd
sealed class PlayerCmd : Cmd

data class SeekAction(val seekTo: Int) : PlayerCmd()
object PlayAction : PlayerCmd()
object PauseAction : PlayerCmd()
object LoadAction : PlayerCmd()
data class InitPlayerAction(val file: SourceFile) : PlayerCmd()
object DeinitPlayerAction : PlayerCmd()
