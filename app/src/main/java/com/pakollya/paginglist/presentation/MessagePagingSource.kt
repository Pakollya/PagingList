package com.pakollya.paginglist.presentation

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.pakollya.paginglist.data.MessagesRepository
import com.pakollya.paginglist.data.cache.message.Message
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MessagePagingSource(
    private val repository: MessagesRepository,
) : PagingSource<Int, Message>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Message> {
        val pageIndex = params.key ?: 0
        val previousIndex = if (pageIndex == 0) null else pageIndex - 1

        return try {
            withContext(Dispatchers.IO) {
                val messages = repository.messagesByPageIndex(pageIndex)
                val isLastPage = repository.isLastPage(pageIndex)
                val nextIndex = if (messages.isEmpty() || isLastPage) null else pageIndex + 1

                LoadResult.Page(
                    data = messages,
                    prevKey = previousIndex,
                    nextKey = nextIndex
                )
            }
        } catch (exception: Exception) {
            LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Message>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}