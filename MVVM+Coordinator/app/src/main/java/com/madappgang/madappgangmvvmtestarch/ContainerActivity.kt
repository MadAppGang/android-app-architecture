package com.madappgang.madappgangmvvmtestarch

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.Toolbar
import androidx.navigation.Navigation
import com.madappgang.madappgangmvvmtestarch.model.models.SourceFile
import com.madappgang.madappgangmvvmtestarch.ui.details.RecordDetailsFragmentArgs
import com.madappgang.madappgangmvvmtestarch.ui.micRecord.MicRecordFragment
import com.madappgang.madappgangmvvmtestarch.ui.micRecord.MicRecordFragmentArgs
import com.madappgang.madappgangmvvmtestarch.ui.recordings.RecordingsFragmentArgs
import kotlinx.android.synthetic.main.activity_container.*
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.KodeinTrigger
import org.kodein.di.android.closestKodein
import org.kodein.di.android.retainedKodein

class ContainerActivity : AppCompatActivity(), Coordinator, KodeinAware {
    override val kodein: Kodein by closestKodein()

    val navigation get() = Navigation.findNavController(this, R.id.container)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_container)
        setSupportActionBar(toolbar)
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

    override fun getToolbar(): Toolbar {
        return toolbar
    }
}

interface Coordinator {
    fun getToolbar(): Toolbar
    fun onSelectRecording(sourceFile: SourceFile)
    fun onSelectFolder(sourceFile: SourceFile)
    fun onCreateRecord(folder: String)
}