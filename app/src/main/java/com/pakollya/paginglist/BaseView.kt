package com.pakollya.paginglist

import android.view.View

interface BaseView {
    fun show(text: CharSequence) = Unit
    fun handleClick(listener: View.OnClickListener) = Unit
    fun select(selected: Boolean) = Unit
}