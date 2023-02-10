package com.example.proverka

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.FileProvider
import com.example.proverka.databinding.ActivityCameraBinding
import java.io.File

/// это не используещиеся активити. сделал чисто чтобы протестить что возвращает камера
class CameraActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCameraBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val file = File(savedInstanceState?.getString("IMAGE"))
        binding = ActivityCameraBinding.inflate(layoutInflater).also { setContentView(it.root) }
        binding.imageView.setImageURI(FileProvider.getUriForFile(this, "com.example.proverka" + ".provider", file))

    }
}