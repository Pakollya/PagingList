package com.pakollya.paginglist.presentation.common

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.paging.PagingData
import com.pakollya.paginglist.data.cache.message.Message

interface Observe {
    fun observeId(owner: LifecycleOwner, observer: Observer<Int>)

    fun observePosition(owner: LifecycleOwner, observer: Observer<Int>)

    fun observeProgress(owner: LifecycleOwner, observer: Observer<Int>)
    
    fun observeData(owner: LifecycleOwner, observer: Observer<PagingData<Message>>)

    fun observeMessages(owner: LifecycleOwner, observer: Observer<List<Message>>)
}