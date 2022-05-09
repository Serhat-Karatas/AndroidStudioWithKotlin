package com.serhat.kotlinlandmarks

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.serhat.kotlinlandmarks.databinding.ActivityDetailsBinding

class DetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailsBinding  //bunlar hazır olarak viewbindingin bize oluşturduğu sınıflar. findview by id ye göre daha hızlıdır.

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val intent = intent
        val landmark = intent.getSerializableExtra("landmark") as Landmark
        binding.nameText.text = landmark.name
        binding.countryText.text = landmark.country
        binding.imageView.setImageResource(landmark.image)
    }
}