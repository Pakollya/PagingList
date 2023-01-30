package com.pakollya.paginglist.presentation.epoxy

import com.airbnb.epoxy.EpoxyModel
import com.airbnb.epoxy.paging3.PagingDataEpoxyController
import com.pakollya.paginglist.data.cache.message.Message
import com.pakollya.paginglist.presentation.common.ClickListener

class MessageController(
    private val clickListener: ClickListener
) : PagingDataEpoxyController<Message>() {

    override fun buildItemModel(currentPosition: Int, item: Message?): EpoxyModel<*> {
        return when (item) {
            null -> {
                PlaceHolderModel_()
                    .id(1L)
            }
            is Message.Header -> {
                HeaderModel_()
                    .id(item.index())
                    .date(item.date)
            }
            else -> {
                MessageModel_()
                    .id(item.index())
                    .message(item)
                    .click(clickListener)
            }
        }
    }
}