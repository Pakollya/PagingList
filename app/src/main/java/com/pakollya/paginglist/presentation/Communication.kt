package com.pakollya.paginglist.presentation

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.paging.PagingData
import com.pakollya.paginglist.data.cache.message.Message
import com.pakollya.paginglist.presentation.common.Observe

interface Communication : Observe, IsSelectedId {

    fun showNewId(id: Int)

    fun showPosition(position: Int)

    fun showProgress(show: Int)

    fun showData(data: PagingData<Message>)

    fun showMessages(messages: List<Message>)

    class Base(
        private val id: MutableLiveData<Int> = MutableLiveData<Int>(),
        private val position: MutableLiveData<Int> = MutableLiveData<Int>(),
        private val progress: MutableLiveData<Int> = MutableLiveData<Int>(),
        private val data: MutableLiveData<PagingData<Message>> = MutableLiveData<PagingData<Message>>(),
        private val messages: MutableLiveData<List<Message>> = MutableLiveData<List<Message>>()
    ) : Communication {
        
        override fun showData(data: PagingData<Message>) {
            this.data.postValue(data)
        }
        
        override fun showNewId(id: Int) {
            this.id.value = id
        }

        override fun showPosition(position: Int) {
            this.position.postValue(position)
        }

        override fun showProgress(show: Int) {
            this.progress.postValue(show)
        }

        override fun showMessages(messages: List<Message>) {
            this.messages.postValue(messages)
        }

        override fun isSelectedId(id: Long): Boolean {
            val value = this.id.value
            return if (value == null)
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

        override fun observeData(owner: LifecycleOwner, observer: Observer<PagingData<Message>>) {
            data.observe(owner, observer)
        }

        override fun observeMessages(owner: LifecycleOwner, observer: Observer<List<Message>>) {
            messages.observe(owner, observer)
        }
    }
}

interface IsSelectedId {
    fun isSelectedId(id: Long): Boolean
}