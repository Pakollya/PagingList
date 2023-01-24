package com.pakollya.paginglist.presentation

import android.view.View.GONE
import android.view.View.VISIBLE
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
    private val communication: Communication,
) : ViewModel(), Load, Observe {

    fun init(isFirstRun: Boolean) {
        communication.mapIsLoading(true)
        communication.showProgress(VISIBLE)
        viewModelScope.launch(Dispatchers.IO) {
            if (isFirstRun)
                repository.init()

            val messages = repository.messages(INIT)

            withContext(Dispatchers.Main) {
                communication.showMessages(messages)
                communication.showProgress(GONE)
            }
        }
    }

    override fun loadNext() {
        if (!repository.isLastPage()) {
            communication.mapIsLoading(true)
            communication.showProgress(VISIBLE)
            viewModelScope.launch(Dispatchers.IO) {
                val messages = repository.messages(NEXT)
                withContext(Dispatchers.Main) {
                    communication.showMessages(messages)
                    communication.showProgress(GONE)
                }
            }
        }
    }

    override fun loadPrevious() {
        if (!repository.isFirstPage()) {
            communication.mapIsLoading(true)
            communication.showProgress(VISIBLE)
            viewModelScope.launch(Dispatchers.IO) {
                val messages = repository.messages(PREVIOUS)
                withContext(Dispatchers.Main) {
                    communication.showMessages(messages)
                    communication.showProgress(GONE)
                }
            }
        }
    }

    fun loadPageById(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = repository.positionOnPageById(id)
            if (repository.changePage(id)) {
                communication.showProgress(VISIBLE)
                val messages = repository.messages(INIT)
                withContext(Dispatchers.Main) {
                    communication.showMessages(messages)
                    communication.showPosition(result)
                    communication.showProgress(GONE)
                }
            } else {
                withContext(Dispatchers.Main) {
                    communication.showPosition(result)
                    communication.showProgress(GONE)
                }
            }
        }
    }

    fun mapId(id: Int) {
        communication.showNewId(id)
    }

    override fun mapIsLoading(isLoading: Boolean) {
        communication.mapIsLoading(isLoading)
    }

    override fun isLoading() = communication.isLoading()

    fun addMessage() {
        viewModelScope.launch(Dispatchers.IO) {
            communication.showProgress(VISIBLE)
            repository.addMessage()
            repository.setLastPage()
            val messages = repository.messages(INIT)
            withContext(Dispatchers.Main) {
                communication.showMessages(messages)
                communication.showProgress(GONE)
            }
            val position = repository.lastPosition()
            communication.showPosition(position)
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

    override fun observeProgress(owner: LifecycleOwner, observer: Observer<Int>) {
        communication.observeProgress(owner, observer)
    }
}