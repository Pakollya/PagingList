package com.pakollya.paginglist

import android.annotation.SuppressLint
import android.view.View
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder

@SuppressLint("NonConstantResourceId")
@EpoxyModelClass(layout = R.layout.header_layout)
public abstract class HeaderModel : EpoxyModelWithHolder<HeaderModel.ViewHolder>() {

    @EpoxyAttribute
    lateinit var date: String

    override fun bind(holder: HeaderModel.ViewHolder) {
        holder.date.show(date)
    }

    class ViewHolder : EpoxyHolder() {
        lateinit var date: CustomTextView

        override fun bindView(itemView: View) {
            date = itemView.findViewById(R.id.header)
        }
    }
}