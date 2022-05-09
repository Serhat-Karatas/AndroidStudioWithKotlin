package com.serhat.kotlinsqlite

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        try{
            val myDatabase=this.openOrCreateDatabase("Psikiyatr", MODE_PRIVATE,null)
            myDatabase.execSQL("CREATE TABLE IF NOT EXISTS psikiyatr(id INTEGER PRIMARY KEY,name VARCHAR,age INT)")

            //myDatabase.execSQL("INSERT INTO psikiyatr(name,age)VALUES ('Alfred adler',67)")
            //myDatabase.execSQL("INSERT INTO psikiyatr(name,age)VALUES ('Sigmund Freud',83)")
            //myDatabase.execSQL("INSERT INTO psikiyatr(name,age)VALUES ('Carl Gustav Jung',86)")


            //myDatabase.execSQL("UPDATE psikiyatr SET age = 61 WHERE name = 'Sigmund Freud'")
            //myDatabase.execSQL("UPDATE psikiyatr SET name = 'Friedrich Nietzsche' WHERE id = 3")

            //myDatabase.execSQL("DELETE FROM psikiyatr WHERE name = 'Lars'")

                                                   //* mean is select all
            //val cursor = myDatabase.rawQuery("SELECT * FROM psikiyatr WHERE name LIKE 'A%'",null)   //sadece A'ile başlayanları getirir
            //val cursor = myDatabase.rawQuery("SELECT * FROM psikiyatr WHERE id = 3",null)           //sadece id si 3 olanı getirir
            val cursor=myDatabase.rawQuery("SELECT * FROM psikiyatr",null)

            val nameIndex=cursor.getColumnIndex("name")
            val ageIndex=cursor.getColumnIndex("age")
            val idIndex = cursor.getColumnIndex("id")

            while (cursor.moveToNext()){
                println("Name: "+cursor.getString(nameIndex))
                println("Age: "+cursor.getInt(ageIndex))
                println("id: "+cursor.getInt(idIndex))
            }
            cursor.close()
        }
        catch (e:Exception){
            e.printStackTrace()
        }

    }

}