package com.pakollya.paginglist

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
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
                    viewModel.loadPageById(150)
                    val position = viewModel.positionById(150)
                    binding.recyclerView.scrollToPosition(position)
                }
            }
        )

        binding.recyclerView.adapter = adapter

        viewModel.init(savedInstanceState == null)

        viewModel.observe(this) {
            adapter.update(it)
        }
    }
}