package com.pakollya.paginglist.presentation.common

import android.view.View

interface BaseView {
    fun show(text: CharSequence) = Unit
    fun handleClick(listener: View.OnClickListener) = Unit
    fun select(selected: Boolean) = Unit
}