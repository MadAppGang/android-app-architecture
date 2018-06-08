package com.madappgang.madappgangmvvmtestarch

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.Navigation
import com.madappgang.madappgangmvvmtestarch.model.models.SourceFile
import com.madappgang.madappgangmvvmtestarch.ui.details.RecordDetailsFragmentArgs
import com.madappgang.madappgangmvvmtestarch.ui.recordings.RecordingsFragmentArgs
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.KodeinTrigger
import org.kodein.di.android.closestKodein
import org.kodein.di.android.retainedKodein

class ContainerActivity : AppCompatActivity(), Coordinator, KodeinAware {
    override val kodein: Kodein by retainedKodein { extend((applicationContext as KodeinAware).kodein) }
    val navigation get() = Navigation.findNavController(this, R.id.container)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_container)
    }

    private fun showRecord(folder: String, id: String) {
        val args = RecordDetailsFragmentArgs.Builder(id, folder).build()
        navigation.navigate(R.id.action_superHeroesFragment2_to_heroDetailsFragment, args.toBundle())
    }

    override fun onSelectFolder(sourceFile: SourceFile) {
        val args = RecordingsFragmentArgs.Builder().setFolder(sourceFile.filePath + "/" + sourceFile.name).build()
        navigation.navigate(R.id.action_superHeroesFragment2_self3, args.toBundle())
    }

    override fun onSelectRecording(sourceFile: SourceFile) {
        showRecord(sourceFile.filePath, sourceFile.id)
    }
}

interface Coordinator {
    fun onSelectRecording(sourceFile: SourceFile)
    fun onSelectFolder(sourceFile: SourceFile)
}