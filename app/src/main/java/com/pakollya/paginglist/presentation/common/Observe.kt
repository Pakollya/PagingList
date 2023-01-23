package com.pakollya.paginglist.presentation.common

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.pakollya.paginglist.presentation.MessagesPageUi

interface Observe {
    fun observeId(owner: LifecycleOwner, observer: Observer<Int>)

    fun observePosition(owner: LifecycleOwner, observer: Observer<Int>)

    fun observeMessages(owner: LifecycleOwner, observer: Observer<MessagesPageUi>)
}