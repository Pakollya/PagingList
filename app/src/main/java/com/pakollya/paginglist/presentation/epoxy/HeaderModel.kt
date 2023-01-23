package com.pakollya.paginglist.presentation.epoxy

import android.annotation.SuppressLint
import android.view.View
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.pakollya.paginglist.R
import com.pakollya.paginglist.presentation.common.CustomTextView

@SuppressLint("NonConstantResourceId")
@EpoxyModelClass(layout = R.layout.header_layout)
public abstract class HeaderModel : EpoxyModelWithHolder<HeaderModel.ViewHolder>() {

    @EpoxyAttribute
    lateinit var date: String

    override fun bind(holder: ViewHolder) {
        holder.date.show(date)
    }

    class ViewHolder : EpoxyHolder() {
        lateinit var date: CustomTextView

        override fun bindView(itemView: View) {
            date = itemView.findViewById(R.id.header)
        }
    }
}