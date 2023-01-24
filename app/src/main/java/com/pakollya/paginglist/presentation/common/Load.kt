package com.pakollya.paginglist.presentation.common

interface Load : LoadNext, LoadPrevious, MapIsLoading, IsLoading
interface LoadNext{
    fun loadNext()
}

interface LoadPrevious{
    fun loadPrevious()
}

interface MapIsLoading {
    fun mapIsLoading(isLoading: Boolean)
}

interface IsLoading {
    fun isLoading(): Boolean
}