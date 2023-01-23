package com.pakollya.paginglist.presentation.epoxy

import android.annotation.SuppressLint
import android.view.View
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.pakollya.paginglist.data.cache.message.Message
import com.pakollya.paginglist.R
import com.pakollya.paginglist.presentation.common.ClickListener
import com.pakollya.paginglist.presentation.common.CustomConstraintLayout
import com.pakollya.paginglist.presentation.common.CustomTextView

@SuppressLint("NonConstantResourceId")
@EpoxyModelClass(layout = R.layout.message_layout)
public abstract class MessageModel : EpoxyModelWithHolder<MessageModel.ViewHolder>() {

    @EpoxyAttribute
    lateinit var message: Message

    @EpoxyAttribute
    lateinit var click: ClickListener

    override fun bind(holder: ViewHolder) {
        message.show(holder.id, holder.content, holder.time, holder.item)
        holder.item.setOnClickListener {
            click.click()
        }
    }

     class ViewHolder : EpoxyHolder() {
        lateinit var id: CustomTextView
        lateinit var content: CustomTextView
        lateinit var time: CustomTextView
        lateinit var item: CustomConstraintLayout

        override fun bindView(itemView: View) {
            id = itemView.findViewById(R.id.id)
            content = itemView.findViewById(R.id.content)
            time = itemView.findViewById(R.id.time)
            item = itemView.findViewById(R.id.messageItem)
        }
    }
}