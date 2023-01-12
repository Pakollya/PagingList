package com.pakollya.paginglist

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer

interface Observe {
    fun observe(owner: LifecycleOwner, observer: Observer<List<Message>>)
}