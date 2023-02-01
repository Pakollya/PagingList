package com.pakollya.paginglist.presentation.common

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.pakollya.paginglist.data.cache.message.Message
import com.pakollya.paginglist.presentation.PageUi

interface Observe {
    fun observeId(owner: LifecycleOwner, observer: Observer<Int>)

    fun observePosition(owner: LifecycleOwner, observer: Observer<Int>)

    fun observeProgress(owner: LifecycleOwner, observer: Observer<Int>)

    fun observeMessagesFlow(owner: LifecycleOwner, observer: Observer<List<Message.Data>>)

    fun observeMessages(owner: LifecycleOwner, observer: Observer<List<PageUi>>)
}