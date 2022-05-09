package com.serhat.kotlincatchtheblack

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.random.Random as Random

class MainActivity : AppCompatActivity() {
    var score =0
    var imageArray=ArrayList<ImageView>()
    var handler=Handler()
    var runnable= Runnable {  }
    lateinit var sharedPreferences:SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        imageArray.add(imageView)
        imageArray.add(imageView2)
        imageArray.add(imageView3)
        imageArray.add(imageView4)
        imageArray.add(imageView5)
        imageArray.add(imageView6)
        imageArray.add(imageView7)
        imageArray.add(imageView8)
        imageArray.add(imageView9)
        hideImages()
        sharedPreferences=this.getSharedPreferences("com.serhat.kotlincatchtheblack", Context.MODE_PRIVATE)
       // sharedPreferences.getInt("max",0)

        object:CountDownTimer(16000,1000){
            override fun onTick(millisUntilFinished: Long) {
                timeText.text="Time :${millisUntilFinished/1000} "
            }

            override fun onFinish() {
             timeText.text="0"
             handler.removeCallbacks(runnable) {
                 for (image in imageArray) {
                     image.visibility = View.INVISIBLE
                 }
             }

             if(score>sharedPreferences.getInt("max",0) ){
                 sharedPreferences.edit().putInt("max",score).apply()
                 Toast.makeText(this@MainActivity,"!!!NEW RECORD!!!",Toast.LENGTH_LONG).show()
             }

             val alert=AlertDialog.Builder(this@MainActivity)
             alert.setTitle("Game Over")
             alert.setMessage("Restart the game?")
             alert.setPositiveButton("Yes"){dialog, which ->
               val intent=intent
               finish()
               startActivity(intent)

             }
             alert.setNegativeButton("No"){dialog, which ->
                 Toast.makeText(this@MainActivity,"game over",Toast.LENGTH_LONG).show()
             }
             alert.show()

            }

        }.start()
    }

    fun ıncreaseScore(vıew: View){   //onclick method for picture
      score++
      scoreText.text="Score : $score"
    }

    fun hideImages(){

        runnable = object:Runnable{
            override fun run() {
                for(image in imageArray) {
                    image.visibility = View.INVISIBLE
                }

                val random= Random
                val ındex=random.nextInt(9)
                imageArray[ındex].visibility=View.VISIBLE
                handler.postDelayed(this,500)        //this or runnable
            }

        }
        /*fun newLocation(){                //or we can do it using single imageview
            runnable = object:Runnable{
            override fun run() {
                val x= Random
                val randomX=x.nextInt(8)*100;
                val randomY=x.nextInt(10)*100;
                handler.postDelayed(this, 400);
                imageView.setX(randomX);
                imageView.setY(randomY);
            }
        };
        handler.post(runnable);

    }*/

        handler.post(runnable)      //started runnable
    }



}