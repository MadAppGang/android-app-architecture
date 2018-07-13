package com.madappgang.madappgangmvvmtestarch.ui.recordings

import android.Manifest
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.view.*
import com.madappgang.madappgangmvvmtestarch.Coordinator
import com.madappgang.madappgangmvvmtestarch.R
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import android.view.MenuInflater
import com.madappgang.madappgangmvvmtestarch.application.GlobalCoordinator
import com.madappgang.madappgangmvvmtestarch.model.useCases.GetRecordingsUseCase
import cz.inventi.elmdroid.createRuntimeFor
import kotlinx.android.synthetic.main.fragment_recordings.*
import org.kodein.di.generic.instance


class RecordingsFragment : Fragment(), KodeinAware {
    override val kodein: Kodein by closestKodein()

    companion object {

        fun newInstance() = RecordingsFragment()
    }

    private lateinit var coordinator: Coordinator
    private lateinit var recordingsAdapter: RecordingsAdapter
    private val repoDefaultFolderName = "Voice Recorder"
    private val folder by lazy {
        val folderArg = RecordingsFragmentArgs.fromBundle(arguments).folder
        return@lazy if (folderArg == "empty") {
            Environment.getExternalStorageDirectory().toString() + "/" + repoDefaultFolderName
        } else {
            folderArg
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        setHasOptionsMenu(true)
        val view = inflater.inflate(R.layout.fragment_recordings, container, false)
        return view
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is Coordinator) {
            coordinator = context
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        init()
    }

    private fun init() {
        val getRecordingsService: GetRecordingsUseCase  by kodein.instance()
        val router: GlobalCoordinator by kodein.instance()
        val configurator = RecordingsComponent.Configurator(folder, router, getRecordingsService)
        val component = RecordingsComponent(configurator)
        val runtime = createRuntimeFor(component)
        recordingsAdapter = RecordingsAdapter()
        recordingsAdapter.clickListener = {
            runtime.dispatch(ItemClicked(it))
        }
        listOfItems.adapter = recordingsAdapter

        runtime.state().observe(this, Observer {
            it?.let {
                progressBar?.visibility = if (it.isLoading) View.VISIBLE else View.GONE
                recordingsAdapter.set(it.recordings)
            }
        })
        val toolbar = coordinator.getToolbar()
        toolbar.title = getString(R.string.RecordingsPage_Title)
        runtime.dispatch(LoadRecords)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.record_file_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        coordinator.onCreateRecord(folder)
        return true
    }

    private fun checkPermissionForReadExtertalStorage(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val result = context?.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
            return result == PackageManager.PERMISSION_GRANTED
        }
        return false
    }

    private val READ_STORAGE_PERMISSION_REQUEST_CODE = 220
    @Throws(Exception::class)
    private fun requestPermissionForReadExternalStorage() {
        try {
            requestPermissions(arrayOf(WRITE_EXTERNAL_STORAGE),
                    READ_STORAGE_PERMISSION_REQUEST_CODE)
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //resume tasks needing this permission
        }
    }
}
