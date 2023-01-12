package com.pakollya.paginglist

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.pakollya.paginglist.MessagesRepository.Strategy.*

class MessagesViewModel(
    private val repository: MessagesRepository = MessagesRepository.Base(),
    private val communication: Communication = Communication.Base(MutableLiveData<List<Message>>())
) : Load, Observe {

    fun init(isFirstRun: Boolean) {
        if (isFirstRun)
            repository.init()

        val list = repository.messages(INIT)
        communication.map(list)
    }

    override fun loadNext() {
        val list = repository.messages(NEXT)
        communication.map(list)
    }

    override fun loadPrevious() {
        val list = repository.messages(PREVIOUS)
        communication.map(list)
    }

    override fun observe(owner: LifecycleOwner, observer: Observer<List<Message>>) {
        communication.observe(owner, observer)
    }
}