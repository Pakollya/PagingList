package com.pakollya.paginglist

import android.content.Context

interface DependencyContainer {

    fun provideCommunication(): Communication

    fun provideMessageFactory(): MessageFactory

    fun provideViewModel(context: Context) : MessagesViewModel

    object Base : DependencyContainer {
        private val communication = Communication.Base()
        private val messageFactory = MessageFactory.Base()

        override fun provideCommunication() = communication

        override fun provideMessageFactory() = messageFactory

        override fun provideViewModel(context: Context) : MessagesViewModel {
            val cache = MessagesCache.Cache(context.applicationContext)

            return MessagesViewModel(
                MessagesRepository.Base(
                    cache.dataBase().messagesDao(),
                    DaysRepository.Base(cache.dataBase().daysDao()),
                    provideMessageFactory()
                ),
                provideCommunication()
            )
        }
    }
}