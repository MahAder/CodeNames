package com.ader.codenames.presentation.start

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.ader.codenames.R
import com.ader.codenames.databinding.DialogMasterKeyBinding

class MasterKeyDialogFragment: DialogFragment() {
    private lateinit var binding: DialogMasterKeyBinding

    var dialogEventListener: DialogEventListener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DialogMasterKeyBinding.inflate(LayoutInflater.from(requireContext()))
        disableApproveBtn()
        binding.masterKeyEt.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                p0?.let {
                    if(it.length < 4){
                        disableApproveBtn()
                    } else {
                        enableApproveButton()
                    }
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })

        binding.approve.setOnClickListener {
            dialogEventListener?.onApproveClick(binding.masterKeyEt.text.toString())
        }

        return AlertDialog.Builder(requireContext())
            .setView(binding.root)
            .create()
    }

    private fun enableApproveButton(){
        binding.approve.alpha = 1f
        binding.approve.isEnabled = true
    }

    private fun disableApproveBtn(){
        binding.approve.alpha = 0.5f
        binding.approve.isEnabled = false
    }

    interface DialogEventListener {
        fun onApproveClick(key: String)
    }
}