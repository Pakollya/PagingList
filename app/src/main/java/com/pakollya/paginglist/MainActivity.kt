package com.pakollya.paginglist

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.pakollya.paginglist.databinding.ActivityMainBinding

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

        binding.addMessageButton.setOnClickListener{
            viewModel.addMessage()
        }

        viewModel.init(savedInstanceState == null)

        viewModel.observeId(this) {
            Toast.makeText(this, "Move to $it item",Toast.LENGTH_LONG).show()
        }

        viewModel.observePosition(this) {
            binding.recyclerView.scrollToPosition(it)
            Log.e("Activity position", "$it")
        }

        viewModel.observeMessages(this) {
            controller.update(it)
        }
    }
}