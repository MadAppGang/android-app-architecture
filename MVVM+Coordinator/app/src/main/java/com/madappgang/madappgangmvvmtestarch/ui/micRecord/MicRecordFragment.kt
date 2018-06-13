package com.madappgang.madappgangmvvmtestarch.ui.micRecord

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
import kotlinx.android.synthetic.main.fragment_details.*
import kotlinx.android.synthetic.main.fragment_mic_record.*
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.provider

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
        val provider = MicRecordViewModelProvider(kodein)
        viewModel = ViewModelProviders.of(this, provider).get(MicRecordViewModel::class.java)
        playRecord.isEnabled = false
        viewModel.recordState.observe(this, Observer {
            playRecord.isEnabled = (it == MicRecordViewModel.RecorderState.pause && viewModel.readTimeMillis.value ?: 0 >= 5000)
            recordPauseButton.setImageResource(if (it == MicRecordViewModel.RecorderState.record) R.drawable.ic_pause_circle_filled_black_24dp else R.drawable.ic_record)
        })
        viewModel.readTimeMillis.observe(this, Observer {
            textView.text = TimeConverters.getTimeFromMillis(it)
        })
        recordFragmentBinding.viewModel = viewModel
    }

}
