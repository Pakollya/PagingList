package com.pakollya.paginglist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pakollya.paginglist.databinding.MessageLayoutBinding
import com.pakollya.paginglist.databinding.NextLayoutBinding
import com.pakollya.paginglist.databinding.PreviousLayoutBinding

class Adapter(private val load: Load) : RecyclerView.Adapter<MessageViewHolder>() {

    private val list = mutableListOf<Message>()

    override fun getItemViewType(position: Int): Int {
        return list[position].type().ordinal
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        return when(viewType) {
            Message.Type.DATA.ordinal -> MessageViewHolder.Base(
                MessageLayoutBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            Message.Type.NEXT.ordinal -> MessageViewHolder.LoadNextMessages(
                NextLayoutBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ),
                load
            )
            Message.Type.PREVIOUS.ordinal -> MessageViewHolder.LoadPreviousMessages(
                PreviousLayoutBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ),
                load
            )

            else -> {throw java.lang.IllegalStateException("MessageViewHolder Creation Exception")}
        }
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int = list.size

    fun update(data: List<Message>) {
        val diffCallback = DiffUtil(list, data)
        val result = androidx.recyclerview.widget.DiffUtil.calculateDiff(diffCallback)
        list.clear()
        list.addAll(data)
        result.dispatchUpdatesTo(this)
    }
}