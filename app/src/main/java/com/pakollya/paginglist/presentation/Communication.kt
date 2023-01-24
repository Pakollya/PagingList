package com.pakollya.paginglist.presentation

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.pakollya.paginglist.presentation.common.Observe

interface Communication: Observe, IsSelectedId, IsLoading {

    fun showNewId(id: Int)

    fun showPosition(position: Int)

    fun showMessages(messages: MessagesPageUi)

    fun showProgress(show: Int)

    fun mapIsLoading(isLoading: Boolean)

    class Base(
        private val id: MutableLiveData<Int> = MutableLiveData<Int>(),
        private val position: MutableLiveData<Int> = MutableLiveData<Int>(),
        private val messages: MutableLiveData<MessagesPageUi> = MutableLiveData<MessagesPageUi>(),
        private val progress: MutableLiveData<Int> = MutableLiveData<Int>(),
        private val isLoading: MutableLiveData<Boolean> = MutableLiveData<Boolean>(false)
    ): Communication {
        override fun showNewId(id: Int) {
            this.id.value = id
        }

        override fun showPosition(position: Int) {
            this.position.value = position
        }

        override fun showMessages(messages: MessagesPageUi) {
            this.messages.value = messages
        }

        override fun showProgress(show: Int) {
            this.progress.postValue(show)
        }

        override fun mapIsLoading(isLoading: Boolean) {
            this.isLoading.value = isLoading
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

        override fun observeMessages(owner: LifecycleOwner, observer: Observer<MessagesPageUi>) {
            messages.observe(owner, observer)
        }

        override fun observeProgress(owner: LifecycleOwner, observer: Observer<Int>) {
            progress.observe(owner, observer)
        }
    }
}

interface IsSelectedId {
    fun isSelectedId(id: Long): Boolean
}

interface IsLoading {
    fun isLoading(): Boolean
}