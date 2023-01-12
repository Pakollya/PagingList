package com.pakollya.paginglist

interface Load : LoadNext, LoadPrevious
interface LoadNext{
    fun loadNext()
}

interface LoadPrevious{
    fun loadPrevious()
}