package com.pakollya.paginglist.presentation

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pakollya.paginglist.data.MessagesRepository
import com.pakollya.paginglist.data.MessagesRepository.Strategy.*
import com.pakollya.paginglist.presentation.common.Load
import com.pakollya.paginglist.presentation.common.Observe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MessagesViewModel(
    private val repository: MessagesRepository,
    private val communication: Communication
) : ViewModel(), Load, Observe {

    fun init(isFirstRun: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            if (isFirstRun)
                repository.init()

            val messages = repository.messages(INIT)

            withContext(Dispatchers.Main) {
                communication.showMessages(messages)
            }
        }
    }

    override fun loadNext() {
        viewModelScope.launch(Dispatchers.IO) {
            //TODO: check page==lastPage
            val messages = repository.messages(NEXT)
            withContext(Dispatchers.Main) {
                communication.showMessages(messages)
            }
        }

    }

    override fun loadPrevious() {
        viewModelScope.launch(Dispatchers.IO) {
            //TODO: check page==0
            val messages = repository.messages(PREVIOUS)
            withContext(Dispatchers.Main) {
                communication.showMessages(messages)
            }
        }
    }

    fun loadPageById(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = repository.positionOnPageById(id)
            if (repository.changePage(id)) {
                val messages = repository.messages(INIT)
                withContext(Dispatchers.Main) {
                    communication.showMessages(messages)
                    communication.showPosition(result)
                }
            } else {
                withContext(Dispatchers.Main) {
                    communication.showPosition(result)
                }
            }
        }
    }

    fun mapId(id: Int) {
        communication.showNewId(id)
    }

    fun addMessage() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addMessage()
            repository.setLastPage()
            val messages = repository.messages(INIT)
            val position = repository.lastPosition()
            withContext(Dispatchers.Main) {
                communication.showMessages(messages)
                communication.showPosition(position)
            }
        }
    }

    override fun observeId(owner: LifecycleOwner, observer: Observer<Int>) {
        communication.observeId(owner, observer)
    }

    override fun observePosition(owner: LifecycleOwner, observer: Observer<Int>) {
        communication.observePosition(owner, observer)
    }

    override fun observeMessages(owner: LifecycleOwner, observer: Observer<MessagesPageUi>) {
        communication.observeMessages(owner, observer)
    }
}