package com.pakollya.paginglist

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.pakollya.paginglist.MessagesRepository.Strategy.*

class MessagesViewModel(
    private val repository: MessagesRepository = MessagesRepository.Base(),
    private val communication: Communication = DependencyContainer.Base.provideCommunication()
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

    fun loadPageById(id: Int) {
        if (repository.changePage(id)) {
            val list = repository.messages()
            communication.map(list)
        }
    }

    fun positionById(id: Int): Int = repository.positionOnPageById(id)

    fun randomId() = repository.randomId()

    fun mapId(id: Int) {
        communication.showNewId(id)
    }

    fun addMessage() {
        repository.addMessage()
        repository.setLastPage()
        val list = repository.messages()
        communication.map(list)
    }

    override fun observeList(owner: LifecycleOwner, observer: Observer<List<Message>>) {
        communication.observeList(owner, observer)
    }

    override fun observeId(owner: LifecycleOwner, observer: Observer<Int>) {
        communication.observeId(owner, observer)
    }
}