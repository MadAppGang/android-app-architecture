package com.madappgang.madappgangmvvmtestarch

import android.arch.lifecycle.Observer
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.Toolbar
import androidx.navigation.Navigation
import com.madappgang.madappgangmvvmtestarch.application.GlobalCoordinator
import com.madappgang.madappgangmvvmtestarch.application.globalCoordinator
import com.madappgang.madappgangmvvmtestarch.model.models.SourceFile
import com.madappgang.madappgangmvvmtestarch.ui.details.RecordDetailsFragmentArgs
import com.madappgang.madappgangmvvmtestarch.ui.micRecord.MicRecordFragmentArgs
import com.madappgang.madappgangmvvmtestarch.ui.recordings.RecordingsFragmentArgs
import kotlinx.android.synthetic.main.activity_container.*
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein

class ContainerActivity : AppCompatActivity(), Coordinator, KodeinAware {


    override val kodein: Kodein by closestKodein()

    val navigation get() = Navigation.findNavController(this, R.id.container)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_container)
        setSupportActionBar(toolbar)
        handleRoutingEvents()
    }

    private fun handleRoutingEvents() {
        applicationContext.globalCoordinator.navigationEventObservable.observe(this, Observer {
            it?.let {
                when (it) {
                    is GlobalCoordinator.NavigationEvent.SelectRecording -> showRecord(it.sourceFile.id)
                    is GlobalCoordinator.NavigationEvent.SelectFolder -> onSelectFolder(it.sourceFile)
                    is GlobalCoordinator.NavigationEvent.onCreateRecord -> onCreateRecord(it.folder)
                    is GlobalCoordinator.NavigationEvent.onBack -> onBack()
                }
            }
        })
    }

    private fun showRecord(id: String) {
        val args = RecordDetailsFragmentArgs.Builder(id).build()
        navigation.navigate(R.id.action_superHeroesFragment2_to_heroDetailsFragment, args.toBundle())
    }

    override fun onSelectFolder(sourceFile: SourceFile) {
        val args = RecordingsFragmentArgs.Builder().setFolder(sourceFile.filePath + "/" + sourceFile.name).build()
        navigation.navigate(R.id.action_superHeroesFragment2_self3, args.toBundle())
    }

    override fun onSelectRecording(sourceFile: SourceFile) {
        showRecord(sourceFile.id)
    }

    override fun onCreateRecord(folder: String) {
        val args = MicRecordFragmentArgs.Builder().setFolderPath(folder).build()
        navigation.navigate(R.id.action_superHeroesFragment2_to_micRecordFragment, args.toBundle())
    }

    override fun onBack() {
        onBackPressed()
    }

    override fun getToolbar(): Toolbar {
        return toolbar
    }
}

interface Coordinator {
    fun getToolbar(): Toolbar
    fun onSelectRecording(sourceFile: SourceFile)
    fun onSelectFolder(sourceFile: SourceFile)
    fun onCreateRecord(folder: String)
    fun onBack()
}