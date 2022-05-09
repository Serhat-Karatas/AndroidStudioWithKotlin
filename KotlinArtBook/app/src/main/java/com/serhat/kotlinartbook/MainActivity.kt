package com.serhat.kotlinartbook

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import com.serhat.kotlinartbook.databinding.ActivityMainBinding
import java.lang.Exception

class MainActivity : AppCompatActivity() {

    private lateinit var binding:ActivityMainBinding
    private lateinit var artList:ArrayList<Art>
    private lateinit var artAdapter : ArtAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        val view=binding.root
        setContentView(view)
        artList=ArrayList<Art>()
        artAdapter = ArtAdapter(artList)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = artAdapter

        try {

            val database = this.openOrCreateDatabase("Arts", Context.MODE_PRIVATE,null)

            val cursor = database.rawQuery("SELECT * FROM arts",null)
            val artNameIx = cursor.getColumnIndex("artname")
            val idIx = cursor.getColumnIndex("id")

            while (cursor.moveToNext()) {
                val name = cursor.getString(artNameIx)
                val id = cursor.getInt(idIx)
                val art = Art(name,id)
                artList.add(art)
            }

            artAdapter.notifyDataSetChanged()

            cursor.close()

        } catch (e: Exception) {
            e.printStackTrace()
        }


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {         //menü oluşturulunca ne yapacağımızı yazıyoruz
        //options yazarsan,options menüyle ilgili pekçok seçenek çıkacaktır.
        //inflater
        val menuInflater=menuInflater         //,hangi menüyü bağlayacağımızı soruyor. Zaten bize parametre olarak veriliyor(menu)
        menuInflater.inflate(R.menu.sanat_menu,menu)  //R.menu. yapınca çıkmazsa File>invalidate cashes / restart yap o zaman çıkacaktır.
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //Tek bir item'imiz var ama yine de hangi item tıklanmış diye kontrol ediyoruz.
        if(item.itemId==R.id.ekle){
            val intent=Intent(this,UploadActivity::class.java)
            intent.putExtra("info","new")
            startActivity(intent)
        }
        return super.onOptionsItemSelected(item)
    }

}