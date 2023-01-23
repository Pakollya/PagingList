package com.pakollya.paginglist

import android.content.Context
import com.pakollya.paginglist.data.MessageFactory
import com.pakollya.paginglist.data.cache.MessagesCache
import com.pakollya.paginglist.data.MessagesRepository
import com.pakollya.paginglist.data.PagesRepository
import com.pakollya.paginglist.presentation.Communication
import com.pakollya.paginglist.presentation.MessagesViewModel

interface DependencyContainer {

    fun provideCommunication(): Communication

    fun provideMessageFactory(): MessageFactory

    fun provideViewModel(context: Context): MessagesViewModel

    object Base : DependencyContainer {
        private val communication = Communication.Base()
        private val messageFactory = MessageFactory.Base()

        override fun provideCommunication() = communication

        override fun provideMessageFactory() = messageFactory

        override fun provideViewModel(context: Context): MessagesViewModel {
            val cache = MessagesCache.Cache(context.applicationContext)

            return MessagesViewModel(
                MessagesRepository.Base(
                    dao = cache.dataBase().messagesDao(),
                    pagesRepository = PagesRepository.Base(
                        messagesDao = cache.dataBase().messagesDao(),
                        dayPartsDao = cache.dataBase().dayPartsDao(),
                        pagesDao = cache.dataBase().pagesDao()
                    ),
                    factory = provideMessageFactory()
                ),
                provideCommunication()
            )
        }
    }
}