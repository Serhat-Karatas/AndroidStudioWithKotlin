package com.serhat.kotlinlandmarks

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
//import com.serhat.kotlinlandmarks.databinding.ActivityDetailsBinding
import com.serhat.kotlinlandmarks.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private  lateinit var landmarkList:ArrayList<Landmark>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        landmarkList=ArrayList<Landmark>()

        val pisa=Landmark("pisa","Italy",R.drawable.pisa)          // Landmark pisa=new Landmark("pisa","Italy",R.drawable.pisa)
        val colosseum=Landmark("colosseum","Italy",R.drawable.colosseum)
        val eiffel=Landmark("eiffel","France",R.drawable.eiffel)
        val londonBridge=Landmark("londonBridge","UK",R.drawable.londonbridge)

        landmarkList.add(pisa)
        landmarkList.add(colosseum)
        landmarkList.add(eiffel)
        landmarkList.add(londonBridge)

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = LandmarkAdapter(landmarkList)
        binding.recyclerView.adapter = adapter
    }



}