package com.ader.eventbudget20.presentation.base

import android.widget.Toast
import androidx.fragment.app.Fragment

abstract class BaseFragment<T: BaseViewModel>: Fragment(){
    abstract val viewModel: T

    fun makeToast(text: String){
        view?.post {
            Toast.makeText(requireContext(), text, Toast.LENGTH_LONG).show()
        }
    }
}