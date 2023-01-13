package com.pakollya.paginglist

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import com.pakollya.paginglist.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val viewModel = MessagesViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        val adapter = Adapter(
            viewModel,
            object : ClickListener {
                override fun click(id: Long) {
                    val randomId = viewModel.randomId()
                    viewModel.mapId(randomId)
                    viewModel.loadPageById(randomId)

                    val position = viewModel.positionById(randomId)
                    binding.recyclerView.scrollToPosition(position)
                }
            }
        )

        binding.recyclerView.adapter = adapter

        binding.addMessageButton.setOnClickListener{
            viewModel.addMessage()
            binding.recyclerView.scrollToPosition(adapter.itemCount - 1)
        }

        viewModel.init(savedInstanceState == null)

        viewModel.observeList(this) {
            adapter.update(it)
        }

        viewModel.observeId(this) {
            Toast.makeText(this, "Move to $it item",Toast.LENGTH_LONG).show()
        }
    }
}