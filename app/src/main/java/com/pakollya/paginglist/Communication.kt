package com.pakollya.paginglist

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer

interface Communication: Observe, IsSelectedId {

    fun showNewId(id: Int)

    fun showPosition(position: Int)

    fun showMessages(messages: MessagesPageUi)

    class Base(
        private val list: MutableLiveData<List<Message>> = MutableLiveData<List<Message>>(),
        private val id: MutableLiveData<Int> = MutableLiveData<Int>(),
        private val position: MutableLiveData<Int> = MutableLiveData<Int>(),
        private val messages: MutableLiveData<MessagesPageUi> = MutableLiveData<MessagesPageUi>()
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
    }
}

interface IsSelectedId {
    fun isSelectedId(id: Long): Boolean
}