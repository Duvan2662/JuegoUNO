package com.example.uno

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.uno.databinding.ActivityPerdedorBinding

class Perdedor : AppCompatActivity() {
    private lateinit var binding: ActivityPerdedorBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPerdedorBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}