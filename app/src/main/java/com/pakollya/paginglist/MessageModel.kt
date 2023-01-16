package com.pakollya.paginglist

import android.annotation.SuppressLint
import android.view.View
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder

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
            click.click(message.id())
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