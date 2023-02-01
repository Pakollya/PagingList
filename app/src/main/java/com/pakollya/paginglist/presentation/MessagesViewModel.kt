package com.pakollya.paginglist.presentation

import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pakollya.paginglist.data.MessagesRepository
import com.pakollya.paginglist.data.MessagesRepository.Strategy
import com.pakollya.paginglist.data.MessagesRepository.Strategy.*
import com.pakollya.paginglist.data.cache.message.Message
import com.pakollya.paginglist.presentation.common.Load
import com.pakollya.paginglist.presentation.common.Observe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MessagesViewModel(
    private val repository: MessagesRepository,
    private val communication: Communication,
) : ViewModel(), Load, Observe {

    fun init(isFirstRun: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            if (isFirstRun)
                repository.init()
        }
    }

    override fun loadMessages(strategy: Strategy, messages: List<Message.Data>) {
        if (strategy == NEXT && repository.isLastPage() || strategy == PREVIOUS && repository.isFirstPage()) {
            return
        }

        communication.showProgress(VISIBLE)
        communication.mapIsLoading(true)
        viewModelScope.launch(Dispatchers.IO) {
            if (strategy == INIT) {
                repository.updatePages(messages)
            }
            val messagesUi = repository.updateMessages(strategy)
            withContext(Dispatchers.Main) {
                communication.showMessages(messagesUi)
                communication.showProgress(GONE)
            }
        }
    }

    fun messages() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.messagesFlow().collectLatest { messages ->
                communication.mapMessages(messages)
            }
        }
    }

    fun loadPageById(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val change = repository.changePage(id)
            val position = repository.positionOnPageById(id)

            if (change) {
                communication.showProgress(VISIBLE)
                val messagesUi = repository.updateMessages()
                communication.showMessages(messagesUi)
            }

            withContext(Dispatchers.Main) {
                communication.showPosition(position)
                communication.showProgress(GONE)
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
            repository.addMessage()
        }
    }

    override fun observeId(owner: LifecycleOwner, observer: Observer<Int>) {
        communication.observeId(owner, observer)
    }

    override fun observePosition(owner: LifecycleOwner, observer: Observer<Int>) {
        communication.observePosition(owner, observer)
    }

    override fun observeProgress(owner: LifecycleOwner, observer: Observer<Int>) {
        communication.observeProgress(owner, observer)
    }

    override fun observeMessagesFlow(
        owner: LifecycleOwner,
        observer: Observer<List<Message.Data>>,
    ) {
        communication.observeMessagesFlow(owner, observer)
    }

    override fun observeMessages(
        owner: LifecycleOwner,
        observer: Observer<List<PageUi>>,
    ) {
        communication.observeMessages(owner, observer)
    }
}