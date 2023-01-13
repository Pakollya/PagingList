package com.pakollya.paginglist

import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.pakollya.paginglist.databinding.MessageLayoutBinding
import com.pakollya.paginglist.databinding.NextLayoutBinding
import com.pakollya.paginglist.databinding.PreviousLayoutBinding

abstract class MessageViewHolder(private val binding: ViewBinding) : RecyclerView.ViewHolder(binding.root) {

    abstract fun bind(message: Message)

    class Base(
        private val binding: MessageLayoutBinding,
        private val clickListener: ClickListener
        ) : MessageViewHolder(binding) {
        override fun bind(message: Message) {
            message.show(binding.id, binding.content, binding.time, binding.messageItem)
            binding.messageItem.setOnClickListener{
                clickListener.click(message.id())
            }
        }
    }

    class LoadNextMessages(
        private val binding: NextLayoutBinding,
        private val load: LoadNext
        ) : MessageViewHolder(binding) {
        override fun bind(message: Message) {
            itemView.setOnClickListener{
                message.handle(load)
            }
        }
    }

    class LoadPreviousMessages(
        private val binding: PreviousLayoutBinding,
        private val load: LoadPrevious
        ) : MessageViewHolder(binding) {
        override fun bind(message: Message) {
            itemView.setOnClickListener{
                message.handle(load)
            }
        }
    }
}