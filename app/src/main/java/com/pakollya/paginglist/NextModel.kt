package com.pakollya.paginglist

import android.annotation.SuppressLint
import android.view.View
import android.widget.Button
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder

@SuppressLint("NonConstantResourceId")
@EpoxyModelClass(layout = R.layout.next_layout)
public abstract class NextModel : EpoxyModelWithHolder<NextModel.ViewHolder>() {

    @EpoxyAttribute
    lateinit var message: Message

    @EpoxyAttribute
    lateinit var load: LoadNext

    override fun bind(holder: ViewHolder) {
        holder.nextButton.setOnClickListener {
            message.handle(load)
        }
    }

    class ViewHolder : EpoxyHolder() {
        lateinit var nextButton: Button

        override fun bindView(itemView: View) {
            nextButton = itemView.findViewById(R.id.nextButton)
        }
    }
}