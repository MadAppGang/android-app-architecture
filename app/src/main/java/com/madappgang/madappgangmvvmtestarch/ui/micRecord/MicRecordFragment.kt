package com.madappgang.madappgangmvvmtestarch.ui.micRecord

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.madappgang.madappgangmvvmtestarch.R

class MicRecordFragment : Fragment() {

    companion object {
        fun newInstance() = MicRecordFragment()
    }

    private lateinit var viewModel: MicRecordViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.mic_record_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(MicRecordViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
