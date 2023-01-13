package com.pakollya.paginglist

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer

interface Observe {
    fun observeList(owner: LifecycleOwner, observer: Observer<List<Message>>)

    fun observeId(owner: LifecycleOwner, observer: Observer<Int>)
}