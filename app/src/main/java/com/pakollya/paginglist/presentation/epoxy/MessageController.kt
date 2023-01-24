package com.pakollya.paginglist.presentation.epoxy

import com.airbnb.epoxy.EpoxyController
import com.pakollya.paginglist.data.cache.message.Message
import com.pakollya.paginglist.data.MessagesRepository.Strategy.*
import com.pakollya.paginglist.presentation.MessagesPageUi
import com.pakollya.paginglist.presentation.common.ClickListener
import com.pakollya.paginglist.presentation.common.Load

class MessageController(
    private val load: Load,
    private val clickListener: ClickListener
) : EpoxyController() {
    private var listPageUi = mutableListOf<MessagesPageUi>()
    private var list = mutableListOf<Message>()

    fun update(ui: MessagesPageUi) {
        when (ui.strategy) {
            INIT -> init(ui)
            NEXT -> next(ui)
            PREVIOUS -> previous(ui)
        }

        requestModelBuild()
    }

    fun init(ui: MessagesPageUi) {
        listPageUi.clear()
        list.clear()

        listPageUi.add(ui)
        list.addAll(ui.messages)
        load.mapIsLoading(false)
    }

    fun next(ui: MessagesPageUi) {
        if (listPageUi.size >= 2) {
            list.subList(0, listPageUi[0].pageSize).clear()
            listPageUi.removeAt(0)
        }
        listPageUi.add(ui)
        list.addAll(ui.messages)
        load.mapIsLoading(false)
    }

    fun previous(ui: MessagesPageUi) {
        if (listPageUi.size >= 2) {
            list.subList(listPageUi[0].pageSize, listPageUi[0].pageSize + listPageUi[1].pageSize).clear()
            listPageUi.removeLast()
        }
        listPageUi.add(0, ui)
        list.addAll(0, ui.messages)
        load.mapIsLoading(false)
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