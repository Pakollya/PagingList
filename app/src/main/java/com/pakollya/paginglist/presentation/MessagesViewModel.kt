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
    private val pager: MessagePager<PagingData<Message>>,
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

    fun messages() {
        viewModelScope.launch(Dispatchers.IO) {
            pager.messagePagingData(viewModelScope).collect { pagingData ->
                communication.showData(pagingData)
            }
        }
    }

    fun messagesById(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val pageIndex = repository.pageIndexById(id)
            pager.messagePagingData(viewModelScope, pageIndex).collect { pagingData ->
                communication.showData(pagingData)
            }
        }
    }

    fun showPosition(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val position = repository.positionOnPageById(id)
            withContext(Dispatchers.Main) {
                communication.showPosition(position)
            }
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
}