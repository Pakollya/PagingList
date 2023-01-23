package com.pakollya.paginglist

import com.airbnb.epoxy.EpoxyController
import com.pakollya.paginglist.MessagesRepository.Strategy.*

class MessageController(
    private val load: Load,
    private val clickListener: ClickListener
) : EpoxyController() {
    private var listPageUi = mutableListOf<MessagesPageUi>()
    private var list = mutableListOf<Message>()

    fun update(ui: MessagesPageUi) {
        when(ui.strategy) {
            INIT -> init(ui)
            NEXT -> loadNext(ui)
            PREVIOUS -> loadPrevious(ui)
        }

        requestModelBuild()
    }

    fun init(ui: MessagesPageUi) {
        listPageUi.clear()
        list.clear()

        listPageUi.add(ui)
        list.addAll(ui.messages)
    }

    fun loadNext(ui: MessagesPageUi) {
        if (listPageUi.size >= 2) {
            list.subList(0, listPageUi[0].pageSize).clear()
            listPageUi.removeAt(0)
        }
        listPageUi.add(ui)
        list.addAll(ui.messages)
    }

    fun loadPrevious(ui: MessagesPageUi) {
        if (listPageUi.size >= 2) {
            list.subList(listPageUi[0].pageSize, listPageUi[0].pageSize +  listPageUi[1].pageSize).clear()
            listPageUi.removeLast()
        }
        listPageUi.add(0, ui)
        list.addAll(0, ui.messages)
    }

    override fun buildModels() {
        list.forEach { item ->
            when (item) {
                is Message.Next -> {
                    next {
                        id(item.index())
                        message(item)
                        load(this@MessageController.load)
                    }
                }
                is Message.Previous -> {
                    previous {
                        id(item.index())
                        message(item)
                        load(this@MessageController.load)
                    }
                }
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