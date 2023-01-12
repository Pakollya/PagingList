package com.pakollya.paginglist

interface Message {

    enum class Type {
        PREVIOUS,
        NEXT,
        DATA
    }

    fun handle(load: LoadNext) = Unit
    fun handle(load: LoadPrevious) = Unit
    fun show(vararg views: BaseView) = Unit
    fun id(): Long
    fun content(): String
    fun type(): Type

    abstract class Abstract(private val type: Type): Message {
        override fun type() = type
        override fun id(): Long = type().ordinal.toLong()
        override fun content(): String = type.toString()
    }

    object Next: Abstract(Type.NEXT) {
        override fun handle(load: LoadNext) = load.loadNext()
    }

    object Previous: Abstract(Type.PREVIOUS) {
        override fun handle(load: LoadPrevious) = load.loadPrevious()

    }
    data class Data(
        val id:Long,
        val content:String,
        val timestamp:String
    ) : Abstract(Type.DATA) {
        //TODO: Add Custom TextView
        override fun show(vararg views: BaseView) {
            views[0].show(id.toString())
            views[1].show(content)
            views[2].show(timestamp)
        }

        override fun id(): Long = id

        override fun content(): String = content
    }
}