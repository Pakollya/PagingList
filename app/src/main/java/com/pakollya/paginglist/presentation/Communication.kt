package com.pakollya.paginglist.presentation

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.pakollya.paginglist.data.cache.message.Message
import com.pakollya.paginglist.presentation.common.Observe

interface Communication: Observe, IsSelectedId, IsLoading {

    fun showNewId(id: Int)

    fun showPosition(position: Int)

    fun showMessages(messages: List<PageUi>)

    fun showProgress(show: Int)

    fun mapIsLoading(isLoading: Boolean)

    fun mapMessages(messages: List<Message.Data>)

    class Base(
        private val id: MutableLiveData<Int> = MutableLiveData<Int>(),
        private val position: MutableLiveData<Int> = MutableLiveData<Int>(),
        private val progress: MutableLiveData<Int> = MutableLiveData<Int>(),
        private val isLoading: MutableLiveData<Boolean> = MutableLiveData<Boolean>(false),
        private val messagesFlow: MutableLiveData<List<Message.Data>> = MutableLiveData<List<Message.Data>>(),
        private val messages: MutableLiveData<List<PageUi>> = MutableLiveData<List<PageUi>>()
    ): Communication {
        override fun showNewId(id: Int) {
            this.id.value = id
        }

        override fun showPosition(position: Int) {
            this.position.postValue(position)
        }

        override fun showProgress(show: Int) {
            this.progress.postValue(show)
        }

        override fun mapIsLoading(isLoading: Boolean) {
            this.isLoading.value = isLoading
        }

        override fun mapMessages(messages: List<Message.Data>) {
            this.messagesFlow.postValue(messages)
        }

        override fun showMessages(messages: List<PageUi>) {
            this.messages.postValue(messages)
        }

        override fun isLoading(): Boolean {
            val value = this.isLoading.value
            return value == true
        }

        override fun isSelectedId(id: Long): Boolean {
            val value = this.id.value
            return if(value == null)
                false
            else
                value.toLong() == id
        }

        override fun observeId(owner: LifecycleOwner, observer: Observer<Int>) {
            id.observe(owner, observer)
        }

        override fun observePosition(owner: LifecycleOwner, observer: Observer<Int>) {
            position.observe(owner, observer)
        }

        override fun observeProgress(owner: LifecycleOwner, observer: Observer<Int>) {
            progress.observe(owner, observer)
        }

        override fun observeMessagesFlow(owner: LifecycleOwner, observer: Observer<List<Message.Data>>) {
            messagesFlow.observe(owner, observer)
        }

        override fun observeMessages(
            owner: LifecycleOwner,
            observer: Observer<List<PageUi>>
        ) {
            messages.observe(owner, observer)
        }
    }
}

interface IsSelectedId {
    fun isSelectedId(id: Long): Boolean
}

interface IsLoading {
    fun isLoading(): Boolean
}