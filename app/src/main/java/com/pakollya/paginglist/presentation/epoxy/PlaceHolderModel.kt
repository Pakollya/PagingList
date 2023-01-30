package com.pakollya.paginglist.presentation.epoxy

import android.annotation.SuppressLint
import android.view.View
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.pakollya.paginglist.R
import com.pakollya.paginglist.presentation.common.CustomConstraintLayout
import com.pakollya.paginglist.presentation.common.CustomTextView

@SuppressLint("NonConstantResourceId")
@EpoxyModelClass(layout = R.layout.message_layout)
abstract class PlaceHolderModel : EpoxyModelWithHolder<PlaceHolderModel.ViewHolder>() {

    override fun bind(holder: ViewHolder) {
        holder.id.show("0")
        holder.content.show("null")
        holder.time.show("null")
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