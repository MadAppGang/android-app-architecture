package com.madappgang.madappgangmvvmtestarch.ui.details

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import com.madappgang.madappgangmvvmtestarch.Coordinator
import com.madappgang.madappgangmvvmtestarch.databinding.FragmentDetailsBinding
import kotlinx.android.synthetic.main.fragment_details.*
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein

class RecordDetailsFragment : Fragment(), KodeinAware {
    override val kodein: Kodein by closestKodein()

    companion object {
        fun newInstance() = RecordDetailsFragment()
    }

    private lateinit var viewModel: RecordDetailsViewModel
    private lateinit var coordinator: Coordinator
    private lateinit var fragmentDetailsBinding: FragmentDetailsBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        fragmentDetailsBinding = FragmentDetailsBinding.inflate(inflater, container, false)
        return fragmentDetailsBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val fromBundle = RecordDetailsFragmentArgs.fromBundle(arguments)
        val provider = RecordingDetailsViewModelProvider(kodein, fromBundle.sourceId)
        viewModel = ViewModelProviders.of(this, provider).get(RecordDetailsViewModel::class.java)
        fragmentDetailsBinding.viewModel = viewModel
        fragmentDetailsBinding.setLifecycleOwner(this)
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                viewModel.seekPosition(progress, fromUser)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }

        })
        viewModel.startPlayRecord()
        // TODO: Use the ViewModel
    }

}
