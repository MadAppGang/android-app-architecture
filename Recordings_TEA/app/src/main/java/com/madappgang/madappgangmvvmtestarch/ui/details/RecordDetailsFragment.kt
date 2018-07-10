package com.madappgang.madappgangmvvmtestarch.ui.details

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import com.madappgang.madappgangmvvmtestarch.Coordinator
import com.madappgang.madappgangmvvmtestarch.R
import com.madappgang.madappgangmvvmtestarch.model.service.PlayerService
import com.madappgang.madappgangmvvmtestarch.model.useCases.GetRecordingUseCase
import cz.inventi.elmdroid.ElmViewModel
import cz.inventi.elmdroid.getViewModelFor
import net.semanticer.renderit.renderit
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.generic.instance

class RecordDetailsFragment : Fragment(), KodeinAware, RecordDetailsView {
    override val kodein: Kodein by closestKodein()

    companion object {
        fun newInstance() = RecordDetailsFragment()
    }

    lateinit var viewModel: ElmViewModel<RecordDetailsState, RecordingMsg, PlayerCmd>
    override fun seekbar(): SeekBar {
        return view!!.findViewById(R.id.seekBar)
    }

    override fun playPause(): ImageView {
        return view!!.findViewById(R.id.playPauseButton)
    }

    override fun seekForward(): ImageView {
        return view!!.findViewById(R.id.seekForwardButton)
    }

    override fun seekBack(): ImageView {
        return view!!.findViewById(R.id.seekBackButton)
    }

    override fun title(): TextView {
        return view!!.findViewById(R.id.title)
    }


    private lateinit var coordinator: Coordinator
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_details, container, false)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val fromBundle = RecordDetailsFragmentArgs.fromBundle(arguments)
        //supportActionBar?.title = getString(R.string.complex_login_with_viewmodel)
        val getRecordingsService: GetRecordingUseCase  by kodein.instance()
        val playerService: PlayerService  by kodein.instance()
        val configurator = RecordingsComponent.Configurator(fromBundle.sourceId, getRecordingsService, playerService)
        val component = RecordingsComponent(configurator)
         viewModel = getViewModelFor(component)
        playPause().setOnClickListener {
            viewModel.dispatch(PlayClicked)
        }
        seekForward().setOnClickListener {
            viewModel.dispatch(SeekForwardClicked)
        }
        seekBack().setOnClickListener {
            viewModel.dispatch(SeekBackwardClicked)
        }
        seekbar().setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    viewModel.dispatch(SeekSelected(progress))
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }

        })
        // observe state
        renderit(viewModel.state(), RecordDetailsRenderer(this))
        viewModel.dispatch(PlayerInitEvent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.dispatch(PlayerDeinitEvent)
    }
}
