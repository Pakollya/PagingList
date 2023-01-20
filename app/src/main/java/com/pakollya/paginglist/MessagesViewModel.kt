package com.pakollya.paginglist

import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pakollya.paginglist.MessagesRepository.Strategy.*
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

            val list = repository.messages(INIT)

            withContext(Dispatchers.Main) {
                communication.map(list)
            }
        }
    }

    override fun loadNext() {
        viewModelScope.launch(Dispatchers.IO) {
            val list = repository.messages(NEXT)
            withContext(Dispatchers.Main) {
                communication.map(list)
                communication.showPosition(0)
            }
        }

    }

    override fun loadPrevious() {
        viewModelScope.launch(Dispatchers.IO) {
            val list = repository.messages(PREVIOUS)
            withContext(Dispatchers.Main) {
                communication.map(list)
                communication.showPosition(list.size - 1)
            }
        }
    }

    fun loadPageById(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = repository.positionOnPageById(id)
            if (repository.changePage(id)) {
                val list = repository.messages()
                withContext(Dispatchers.Main) {
                    communication.map(list)
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
            val list = repository.messages()
            val position = repository.lastPosition()
            withContext(Dispatchers.Main) {
                communication.map(list)
                communication.showPosition(position)
            }
        }
    }

    override fun observeList(owner: LifecycleOwner, observer: Observer<List<Message>>) {
        communication.observeList(owner, observer)
    }

    override fun observeId(owner: LifecycleOwner, observer: Observer<Int>) {
        communication.observeId(owner, observer)
    }

    override fun observePosition(owner: LifecycleOwner, observer: Observer<Int>) {
        communication.observePosition(owner, observer)
    }
}