package com.ader.eventbudget20.presentation.base

import androidx.fragment.app.DialogFragment

abstract class BaseDialogFragment<T: BaseViewModel>: DialogFragment() {
    abstract val viewModel: T
}