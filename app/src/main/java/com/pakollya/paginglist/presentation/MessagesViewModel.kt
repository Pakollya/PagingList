package com.pakollya.paginglist.presentation

import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.lifecycle.*
import androidx.paging.PagingData
import com.pakollya.paginglist.data.MessagesRepository
import com.pakollya.paginglist.data.cache.message.Message
import com.pakollya.paginglist.presentation.common.Observe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MessagesViewModel(
    private val repository: MessagesRepository,
    private val communication: Communication,
    private val messagePager: MessagePager<PagingData<Message>>,
) : ViewModel(), Observe {

    fun init(isFirstRun: Boolean) {
        communication.showProgress(VISIBLE)
        viewModelScope.launch(Dispatchers.IO) {
            if (isFirstRun)
                repository.init()

            withContext(Dispatchers.Main) {
                communication.showProgress(GONE)
            }
        }
    }

    fun messagesFlow() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.messagesFlow().collect { messages ->
                communication.showMessages(messages)
            }
        }
    }

    fun messages() {
        viewModelScope.launch(Dispatchers.IO) {
            messagePager.messagePagingData(viewModelScope).collect { pagingData ->
                communication.showData(pagingData)
            }
        }
    }

    fun updatePages() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updatePages()
        }
    }

    fun messagesById(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val position = repository.positionOnPageById(id)
            communication.showPosition(position)
            //TODO: подумать есть ли еще способы перейти на необходимую страницу используя Pager
//            messagePager.messagePagingData(viewModelScope, position).collect { pagingData ->
//                communication.showData(pagingData)
//            }
        }
    }

    fun addMessage() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addMessage()
        }
    }

    fun mapId(id: Int) {
        communication.showNewId(id)
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

    override fun observeData(owner: LifecycleOwner, observer: Observer<PagingData<Message>>) {
        communication.observeData(owner, observer)
    }

    override fun observeMessages(owner: LifecycleOwner, observer: Observer<List<Message>>) {
        communication.observeMessages(owner, observer)
    }
}