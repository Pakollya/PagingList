package com.pakollya.paginglist.presentation

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pakollya.paginglist.presentation.common.Load

class MessagesScrollListener(
    private val manager: LinearLayoutManager,
    private val load: Load
) : RecyclerView.OnScrollListener() {

    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        super.onScrollStateChanged(recyclerView, newState)
    }

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)

        val visibleItemCount = manager.childCount
        val totalItemCount = manager.itemCount
        val pastVisibleItems = manager.findFirstVisibleItemPosition()

        if (!load.isLoading()) {
            if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                load.loadNext()
            }

            if (pastVisibleItems <= visibleItemCount) {
                load.loadPrevious()
            }
        }
    }
}