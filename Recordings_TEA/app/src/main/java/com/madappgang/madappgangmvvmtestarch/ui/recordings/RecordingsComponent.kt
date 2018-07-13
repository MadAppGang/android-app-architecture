package com.madappgang.madappgangmvvmtestarch.ui.recordings


import com.madappgang.madappgangmvvmtestarch.application.GlobalCoordinator
import com.madappgang.madappgangmvvmtestarch.model.models.SourceFile
import com.madappgang.madappgangmvvmtestarch.model.useCases.GetRecordingsUseCase
import com.madappgang.madappgangmvvmtestarch.utils.Result
import cz.inventi.elmdroid.*
import io.reactivex.Single

class RecordingsComponent(private val configurator: Configurator) : Component<RecordDetailsState, RecordingsMsg, RecordingsCmd> {
    class Configurator(
            val folderPath: String,
            val coordinator: GlobalCoordinator,
            val getRecordingsUseCaseService: GetRecordingsUseCase)

    override fun initState(): RecordDetailsState = RecordDetailsState(true, emptyList())

    override fun update(msg: RecordingsMsg, prevState: RecordDetailsState): Pair<RecordDetailsState, RecordingsCmd?> = when (msg) {
        Nothing -> prevState to null
        is ItemClicked -> prevState.withCmd(HandleItemSelect(msg.file))
        LoadRecords -> prevState.copy(isLoading = true).withCmd(LoadRecordsCmd(configurator.folderPath))
        LoadingFinished -> prevState.copy(isLoading = false).noCmd()
        is ShowRecords -> prevState.copy(recordings = msg.list).noCmd()
        is ShowError -> TODO()
        PlayRecordClicked -> TODO()
        AddRecordClicked -> TODO()
    }


    override fun call(cmd: RecordingsCmd): Single<RecordingsMsg> = when (cmd) {
        is LoadRecordsCmd -> getRecording(configurator.folderPath, configurator.getRecordingsUseCaseService)
        is HandleItemSelect -> handleItemClick(cmd.file, configurator.coordinator)
    }

    override fun subscriptions(): List<Sub<RecordDetailsState, RecordingsMsg>> = listOf(RecordingsSubscription(configurator.getRecordingsUseCaseService))
}


// Tasks
fun getRecording(folder: String, getRecordingsUseCaseService: GetRecordingsUseCase): Single<RecordingsMsg> {
    getRecordingsUseCaseService.invoke(folder)
    return Single.just(LoadingFinished)
}

fun handleItemClick(item: SourceFile, coordinator: GlobalCoordinator): Single<RecordingsMsg> {
    if (item.isDirectory) {
        coordinator.dispatch(GlobalCoordinator.NavigationEvent.SelectFolder(item))
    } else {
        coordinator.dispatch(GlobalCoordinator.NavigationEvent.SelectRecording(item))
    }
    return Single.just(Nothing)
}

// Subscriptions
class RecordingsSubscription(val getRecordingsUseCaseService: GetRecordingsUseCase) : StatelessSub<RecordDetailsState, RecordingsMsg>() {
    override fun invoke(): io.reactivex.Observable<RecordingsMsg> = getRecordingsUseCaseService.recordings.map {
        when (it) {
            is Result.Success -> ShowRecords(it.value)
            is Result.Error -> ShowError(it.error)
        }
    }
}

// State
data class RecordDetailsState(
        val isLoading: Boolean,
        val recordings: List<SourceFile>
) : State

// Msg
sealed class RecordingsMsg : Msg

object LoadRecords : RecordingsMsg()
object LoadingFinished : RecordingsMsg()
data class ShowRecords(val list: List<SourceFile>) : RecordingsMsg()
data class ShowError(val e: Throwable) : RecordingsMsg()
data class ItemClicked(val file: SourceFile) : RecordingsMsg()
object Nothing : RecordingsMsg()
object PlayRecordClicked : RecordingsMsg()
object AddRecordClicked : RecordingsMsg()

// Cmd
sealed class RecordingsCmd : Cmd

data class LoadRecordsCmd(val folder: String) : RecordingsCmd()
data class HandleItemSelect(val file: SourceFile) : RecordingsCmd()

