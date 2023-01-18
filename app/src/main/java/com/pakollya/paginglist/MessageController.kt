package com.pakollya.paginglist

import com.airbnb.epoxy.EpoxyController

class MessageController(
    private val load: Load,
    private val clickListener: ClickListener
) : EpoxyController() {
    private var list = mutableListOf<Message>()

    fun update(data: List<Message>) {
        list.clear()
        list.addAll(data)

        requestModelBuild()
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