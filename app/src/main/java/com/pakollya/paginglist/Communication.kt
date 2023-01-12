package com.pakollya.paginglist

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer

interface Communication: Observe {

    fun map(list: List<Message>)

    class Base(
        private val liveData: MutableLiveData<List<Message>> = MutableLiveData<List<Message>>()
    ): Communication {
        override fun map(list: List<Message>) {
            liveData.value = list
        }

        override fun observe(owner: LifecycleOwner, observer: Observer<List<Message>>) {
            liveData.observe(owner, observer)
        }

    }
}