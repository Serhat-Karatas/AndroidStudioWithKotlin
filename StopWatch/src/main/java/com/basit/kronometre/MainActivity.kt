package com.basit.kronometre

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    //val textView=findViewById<TextView>(R.id.textView)  //kotlin's suggestion for view binding
    var number=0
    var runnable:Runnable= Runnable {  }
    var handler:Handler=Handler(Looper.getMainLooper())  //if this syntax give error; you try "var handler:Handler=Handler(Looper.getMainLooper())"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun start(view: View){
        number=0
        runnable=object : Runnable{

            override fun run() {     //things that run periodically in the background
                number++
                textView.text="Time :$number"
                handler.postDelayed(this,1000)     //frequency, this->runnable ,not main activity
            }
        }
        handler.post(runnable)         //runnable is started
    }

    fun stop(view:View){
       handler.removeCallbacks(runnable)
       number=0
        textView.text="Time : 0"
    }


}