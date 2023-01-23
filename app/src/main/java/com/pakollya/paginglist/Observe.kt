package com.pakollya.paginglist

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer

interface Observe {
    fun observeId(owner: LifecycleOwner, observer: Observer<Int>)

    fun observePosition(owner: LifecycleOwner, observer: Observer<Int>)

    fun observeMessages(owner: LifecycleOwner, observer: Observer<MessagesPageUi>)
}