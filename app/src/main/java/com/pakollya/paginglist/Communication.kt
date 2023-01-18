package com.pakollya.paginglist

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer

interface Communication: Observe, IsSelectedId {

    fun map(list: List<Message>)

    fun showNewId(id: Int)

    fun showPosition(position: Int)

    class Base(
        private val list: MutableLiveData<List<Message>> = MutableLiveData<List<Message>>(),
        private val id: MutableLiveData<Int> = MutableLiveData<Int>(),
        private val position: MutableLiveData<Int> = MutableLiveData<Int>()
    ): Communication {
        override fun map(list: List<Message>) {
            this.list.value = list
        }

        override fun showNewId(id: Int) {
            this.id.value = id
        }

        override fun showPosition(position: Int) {
            this.position.value = position
        }

        override fun isSelectedId(id: Long): Boolean {
            val value = this.id.value
            return if(value == null)
                false
            else
                value.toLong() == id
        }

        override fun observeList(owner: LifecycleOwner, observer: Observer<List<Message>>) {
            list.observe(owner, observer)
        }

        override fun observeId(owner: LifecycleOwner, observer: Observer<Int>) {
            id.observe(owner, observer)
        }

        override fun observePosition(owner: LifecycleOwner, observer: Observer<Int>) {
            position.observe(owner, observer)
        }
    }
}

interface IsSelectedId {
    fun isSelectedId(id: Long): Boolean
}