package com.madappgang.madappgangmvvmtestarch.ui.micRecord

import android.Manifest
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.madappgang.madappgangmvvmtestarch.R

import com.madappgang.madappgangmvvmtestarch.databinding.FragmentMicRecordBinding
import com.madappgang.madappgangmvvmtestarch.model.useCases.RecordDataUseCase
import com.madappgang.madappgangmvvmtestarch.utils.timeUtils.TimeConverters
import kotlinx.android.synthetic.main.fragment_mic_record.*
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.provider
import android.content.Context
import android.support.v7.app.AlertDialog
import android.text.InputType
import android.widget.EditText
import com.madappgang.madappgangmvvmtestarch.Coordinator
import android.app.Activity
import android.support.v4.app.ActivityCompat
import android.os.Build
import android.content.pm.PackageManager
import android.widget.Toast


class MicRecordFragment : Fragment(), KodeinAware {
    private val module by lazy {
        Kodein.Module {
            val args = MicRecordFragmentArgs.fromBundle(arguments)
            bind<RecordDataUseCase>() with provider { RecordDataUseCase(args.folderPath) }
        }
    }
    private val _parentKodein by closestKodein()
    override val kodein: Kodein by lazy {
        Kodein {
            extend(_parentKodein)
            import(module)
        }
    }

    companion object {
        fun newInstance() = MicRecordFragment()
    }

    private lateinit var viewModel: MicRecordViewModel
    private lateinit var recordFragmentBinding: FragmentMicRecordBinding
    private lateinit var coordinator: Coordinator
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        recordFragmentBinding = FragmentMicRecordBinding.inflate(inflater, container, false)
        return recordFragmentBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        requestWritePermission { initViewModel() }
    }

    private fun initViewModel() {
        val provider = MicRecordViewModelProvider(kodein)
        viewModel = ViewModelProviders.of(this, provider).get(MicRecordViewModel::class.java)
        playRecord.isEnabled = false
        viewModel.saveAction.observe(this, Observer {
            when (it) {
                is MicRecordViewModel.SaveAction.SaveActionSuccess -> {
                    coordinator.onBack()
                }
                is MicRecordViewModel.SaveAction.ShowSaveAction -> {
                    showEnterFileNameDialog()
                }
            }
        })
        viewModel.recordState.observe(this, Observer {
            playRecord.isEnabled = (it == MicRecordViewModel.RecorderState.paused && viewModel.readTimeMillis.value ?: 0 >= 5000)
            recordPauseButton.setImageResource(if (it == MicRecordViewModel.RecorderState.recording) R.drawable.ic_pause_circle_filled_black_24dp else R.drawable.ic_record)
        })
        viewModel.readTimeMillis.observe(this, Observer {
            textView.text = TimeConverters.getTimeFromMillis(it)
        })
        recordFragmentBinding.viewModel = viewModel
    }

    val PERMITTIONS_REQUEST = 112
    fun requestWritePermission(result: () -> Unit) {
        if (Build.VERSION.SDK_INT >= 23) {
            val PERMISSIONS_ARRAY = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE , Manifest.permission.RECORD_AUDIO)
            if (!hasPermissions(context, Manifest.permission.WRITE_EXTERNAL_STORAGE , Manifest.permission.RECORD_AUDIO)) {
                ActivityCompat.requestPermissions(context as Activity, PERMISSIONS_ARRAY, PERMITTIONS_REQUEST)
            } else {
                result.invoke()
            }
        } else {
            result.invoke()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMITTIONS_REQUEST -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initViewModel()
                } else {
                    Toast.makeText(context, "The app was not allowed to write in your storage", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun Fragment.hasPermissions(context: Context?, vararg permissions: String): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (permission in permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false
                }
            }
        }
        return true
    }

    fun showEnterFileNameDialog() {
        val builder = AlertDialog.Builder(context!!)
        builder.setTitle("Title")

// Set up the input
        val input = EditText(context!!)
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.inputType = InputType.TYPE_CLASS_TEXT
        builder.setView(input)

// Set up the buttons
        builder.setPositiveButton("OK") { dialog, which ->
            val name = input.text.toString()
            if (name.isNotEmpty()) {
                viewModel.saveWithName(name)
            }
        }
        builder.setNegativeButton("Cancel") { dialog, which -> dialog.cancel() }

        builder.show()
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is Coordinator) {
            coordinator = context
        }
    }

}
