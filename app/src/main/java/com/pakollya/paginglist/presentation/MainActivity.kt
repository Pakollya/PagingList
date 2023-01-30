package com.pakollya.paginglist.presentation

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.pakollya.paginglist.DependencyContainer
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

        viewModel.init(savedInstanceState == null)


        val controller = MessageController(
            object : ClickListener {
                override fun click() {
                    val randomId = 20900
                    viewModel.messagesById(randomId)
                    viewModel.mapId(randomId)
                    viewModel.showPosition(randomId)
                }
            }
        )

        binding.recyclerView.adapter = controller.adapter

        viewModel.messages()

        binding.addMessageButton.setOnClickListener{
            viewModel.addMessage()
        }

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
    }
}