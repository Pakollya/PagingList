package com.pakollya.paginglist.presentation

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.pakollya.paginglist.DependencyContainer
import com.pakollya.paginglist.data.MessagesRepository.Strategy.INIT
import com.pakollya.paginglist.databinding.ActivityMainBinding
import com.pakollya.paginglist.presentation.common.ClickListener
import com.pakollya.paginglist.presentation.epoxy.MessageController

class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: MessagesViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        viewModel = DependencyContainer.Base.provideViewModel(this.applicationContext)

        val controller = MessageController(
            viewModel,
            object : ClickListener {
                override fun click() {
                    val randomId = 20900
                    viewModel.mapId(randomId)
                    viewModel.loadPageById(randomId)
                }
            }
        )

        binding.recyclerView.adapter = controller.adapter

        val scrollListener = MessagesScrollListener(
            manager = binding.recyclerView.layoutManager as LinearLayoutManager,
            load = viewModel
        )
        binding.recyclerView.addOnScrollListener(scrollListener)

        binding.addMessageButton.setOnClickListener{
            viewModel.addMessage()
        }

        viewModel.init(savedInstanceState == null)

        viewModel.messages()

        viewModel.observeId(this) {
            Toast.makeText(this, "Move to $it item",Toast.LENGTH_LONG).show()
        }

        viewModel.observePosition(this) {
            binding.recyclerView.scrollToPosition(it)
            Log.e("Activity position", "$it")
        }

        viewModel.observeProgress(this) {
            binding.progress.visibility = it
        }

        viewModel.observeMessagesFlow(this) {
            Log.e("Activity", "Flow")
            viewModel.loadMessages(INIT, it)
        }

        viewModel.observeMessages(this) { messagesUi ->
            controller.load(messagesUi)
        }
    }
}