package com.example.uno

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.uno.databinding.GanadorBinding


class Ganador : AppCompatActivity() {
    private lateinit var binding: GanadorBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = GanadorBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}