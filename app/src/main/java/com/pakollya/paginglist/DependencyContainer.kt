package com.pakollya.paginglist

interface DependencyContainer {

    fun provideCommunication(): Communication

    fun provideCache(): MessageCache

    object Base: DependencyContainer {
        private val communication = Communication.Base()
        private val messageCache = MessageCache.Base()

        override fun provideCommunication() = communication

        override fun provideCache() = messageCache
    }
}