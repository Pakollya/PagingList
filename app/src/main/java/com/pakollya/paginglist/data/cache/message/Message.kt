package com.pakollya.paginglist.data.cache.message

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.pakollya.paginglist.DependencyContainer
import com.pakollya.paginglist.presentation.common.BaseView
import com.pakollya.paginglist.presentation.IsSelectedId
import java.text.SimpleDateFormat
import java.util.*

interface Message {
    fun show(vararg views: BaseView) = Unit
    fun index(): Long
    fun messageId(): Long = -1L

    data class Header(val date: String) : Message {
        override fun index() = 2L
    }

    @Entity(tableName = "messages")
    data class Data(
        @PrimaryKey
        val id:Long,
        val content:String,
        val timestamp:Long,
    ) : Message {
        @Ignore
        val isSelected: IsSelectedId = DependencyContainer.Base.provideCommunication()

        @Ignore
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        override fun show(vararg views: BaseView) {
            views[0].show(id.toString())
            views[1].show(content)
            views[2].show(dateFormat.format(Date(timestamp)))
            views[3].select(isSelected.isSelectedId(id))
        }

        override fun index(): Long = id + 3L

        override fun messageId() = id
    }
}