package com.pakollya.paginglist.presentation.epoxy

import com.airbnb.epoxy.EpoxyController
import com.pakollya.paginglist.data.cache.message.Message
import com.pakollya.paginglist.presentation.PageUi
import com.pakollya.paginglist.presentation.common.ClickListener
import com.pakollya.paginglist.presentation.common.Load

class MessageController(
    private val load: Load,
    private val clickListener: ClickListener
) : EpoxyController() {
    private var list = mutableListOf<Message>()

    fun load(listUi: List<PageUi>) {
        if (listUi.isNotEmpty()) {
            list.clear()

            listUi.forEach { ui ->
                list.addAll(ui.messages)
            }
        }

        load.mapIsLoading(false)
        requestModelBuild()
    }

    override fun buildModels() {
        list.forEach { item ->
            when (item) {
                is Message.Header -> {
                    header {
                        id(item.index())
                        date(item.date)
                    }
                }
                is Message.Data -> {
                    message {
                        id(item.index())
                        message(item)
                        click(this@MessageController.clickListener)
                    }
                }
            }
        }
    }
}