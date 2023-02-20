package ru.samsung.case2022.ui

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.FileProvider
import ru.samsung.case2022.databinding.ActivityCameraBinding
import java.io.File

/// это не используещиеся активити. сделал чисто чтобы протестить что возвращает камера
class CameraActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCameraBinding
    private lateinit var savedInrent: Bundle


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraBinding.inflate(layoutInflater).also { setContentView(it.root) }
        binding.cancel.setOnClickListener(){onCanselPressed()}
        binding.recognize.setOnClickListener(){onRecognizePressed()}
        binding.preview.setImageBitmap(savedInstanceState?.getBundle("data") as Bitmap)
        savedInrent = savedInstanceState

    }

    private fun onRecognizePressed() {
        val intent = Intent()
        intent.putExtra("data", savedInrent?.getBundle("data"))
        setResult(RESULT_OK, intent)
        finish()
    }

    private fun onCanselPressed() {
        finish()
    }
}