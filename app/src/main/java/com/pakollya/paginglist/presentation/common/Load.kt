package com.pakollya.paginglist.presentation.common

interface Load : LoadNext, LoadPrevious
interface LoadNext{
    fun loadNext()
}

interface LoadPrevious{
    fun loadPrevious()
}