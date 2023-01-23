package com.pakollya.paginglist.presentation.epoxy

import android.annotation.SuppressLint
import android.view.View
import android.widget.Button
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.pakollya.paginglist.data.cache.message.Message
import com.pakollya.paginglist.R
import com.pakollya.paginglist.presentation.common.LoadPrevious

@SuppressLint("NonConstantResourceId")
@EpoxyModelClass(layout = R.layout.previous_layout)
public abstract class PreviousModel : EpoxyModelWithHolder<PreviousModel.ViewHolder>() {

    @EpoxyAttribute
    lateinit var message: Message

    @EpoxyAttribute
    lateinit var load: LoadPrevious

    override fun bind(holder: ViewHolder) {
        holder.previousButton.setOnClickListener {
            message.handle(load)
        }
    }

    class ViewHolder : EpoxyHolder() {
        lateinit var previousButton: Button

        override fun bindView(itemView: View) {
            previousButton = itemView.findViewById(R.id.previousButton)
        }
    }
}