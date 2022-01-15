package com.ader.codenames.presentation.start

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.ader.codenames.R
import com.ader.codenames.databinding.FragmentStartBinding
import com.ader.eventbudget20.presentation.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StartFragment: BaseFragment<StartFragmentViewModel>() {
    private lateinit var binding: FragmentStartBinding

    override val viewModel: StartFragmentViewModel by viewModels()

    private lateinit var progressDialog: AlertDialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadInfo()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progressDialog = createProgressDialog()
        binding.startNewGameBtn.setOnClickListener {
            showMasterKeyInputDialog()
        }

        viewModel.isGameSaved.observe(viewLifecycleOwner, {
            if(it){
                binding.continueGame.setOnClickListener {
                    viewModel.loadSavedGame()
                }
            } else {
                binding.continueGame.alpha = 0.5f
            }
        })

        viewModel.openGameLiveData.observe(viewLifecycleOwner, {
            if(it) {
                findNavController().navigate(R.id.action_startFragment_to_gameFragment)
                viewModel.redirectionComplete()
            }
        })

        viewModel.loadingLiveData.observe(viewLifecycleOwner, {
            if(it){
                progressDialog.show()
            } else {
                progressDialog.dismiss()
            }
        })
    }

    private fun showMasterKeyInputDialog(){
        val dialog = MasterKeyDialogFragment()
        dialog.dialogEventListener = object : MasterKeyDialogFragment.DialogEventListener {
            override fun onApproveClick(key: String) {
                viewModel.startNewGame(key)
                dialog.dismiss()
            }
        }
        dialog.show(childFragmentManager, "")
    }

    private fun createProgressDialog(): AlertDialog{
        return AlertDialog.Builder(requireContext())
            .setView(ProgressBar(requireContext()))
            .setCancelable(false)
            .create()
    }
}