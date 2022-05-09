package com.serhat.kotlinartbook

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.serhat.kotlinartbook.databinding.ActivityUploadBinding
import java.io.ByteArrayOutputStream
import java.io.IOException

class UploadActivity : AppCompatActivity() {

    private lateinit var binding:ActivityUploadBinding
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>    //sonucunda bir veri alacağın bir aktiviteyi başlat. Galeriye gitmek için kullanacağımız değişken.
    private lateinit var izinBaslatici: ActivityResultLauncher<String>             // içerisinde string olacak çünkü izinler string tiptedir.ActivityResultLauncher ları onCreate de initialize etmeden kullanamazsın uygulama çöker.
    private lateinit var database : SQLiteDatabase
    var selectedBitmap : Bitmap? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityUploadBinding.inflate(layoutInflater)
        val view=binding.root
        setContentView(view)
        database = this.openOrCreateDatabase("Arts", Context.MODE_PRIVATE,null)


        registerLauncher()
         val intent = intent

        val info = intent.getStringExtra("info")

        if (info.equals("new")) {     //yeni sanat eseri oluştur
            binding.artNameText.setText("")
            binding.artistNameText.setText("")
            binding.yearText.setText("")
            binding.button.visibility = View.VISIBLE
            //binding.imageView.setImageResource(R.drawable.)
            val selectedImageBackground = BitmapFactory.decodeResource(applicationContext.resources,R.drawable.select)
            binding.imageView.setImageBitmap(selectedImageBackground)

        } else {       //oluşturulmuş sanat eserini göster
            binding.button.visibility = View.INVISIBLE
            val selectedId = intent.getIntExtra("id",1)

            val cursor = database.rawQuery("SELECT * FROM arts WHERE id = ?", arrayOf(selectedId.toString()))

            val artNameIx = cursor.getColumnIndex("artname")
            val artistNameIx = cursor.getColumnIndex("artistname")
            val yearIx = cursor.getColumnIndex("year")
            val imageIx = cursor.getColumnIndex("image")

            while (cursor.moveToNext()) {
                binding.artNameText.setText(cursor.getString(artNameIx))
                binding.artistNameText.setText(cursor.getString(artistNameIx))
                binding.yearText.setText(cursor.getString(yearIx))

                val byteArray = cursor.getBlob(imageIx)
                val bitmap = BitmapFactory.decodeByteArray(byteArray,0,byteArray.size)
                binding.imageView.setImageBitmap(bitmap)

            }

            cursor.close()
            binding.artNameText.isEnabled=false
            binding.artistNameText.isEnabled=false
            binding.yearText.isEnabled=false
            binding.imageView.isEnabled=false

        }


    }

    fun saveButtonClicked(view:View){
        val artName = binding.artNameText.text.toString()                //bu 3 değişkeni varchar olarak veritabanına yazabiliriz ancak
        val artistName = binding.artistNameText.text.toString()          //fotoğrafı kaydetmek için bitlere çevirmemiz gerek.
        val year = binding.yearText.text.toString()

        if (selectedBitmap != null) {             //!!->kesinlikele null değil demek.
            val smallBitmap = makeSmallerBitmap(selectedBitmap!!,300)

            val outputStream = ByteArrayOutputStream()        //byte dizisi oluşturmak için kullandığımız bir yardımcı sınıf
            smallBitmap.compress(Bitmap.CompressFormat.PNG,50,outputStream)
            val byteArray = outputStream.toByteArray()

            try {
                database.execSQL("CREATE TABLE IF NOT EXISTS arts (id INTEGER PRIMARY KEY, artname VARCHAR, artistname VARCHAR, year VARCHAR, image BLOB)")

                val sqlString = "INSERT INTO arts (artname, artistname, year, image) VALUES (?, ?, ?, ?)"  //değeri değişken olan şeyleri veri tabanına yazmak.---{
                val statement = database.compileStatement(sqlString)
                statement.bindString(1, artName)
                statement.bindString(2, artistName)
                statement.bindString(3, year)
                statement.bindBlob(4, byteArray)

                statement.execute()                                                                       //-------------------------}

            } catch (e: Exception) {
                e.printStackTrace()
            }


            val intent = Intent(this,MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)

            //finish()
        }
    }
                  //küçültmek istenilen bitmap               //ne döndürecek
    fun makeSmallerBitmap(image: Bitmap, maximumSize : Int) : Bitmap {
        var genislik = image.width
        var yukseklik = image.height

        val bitmapOran : Double = genislik.toDouble() / yukseklik.toDouble()
        if (bitmapOran > 1) { //oran 1 den büyükse görsel yatay
            genislik = maximumSize
            val scaledHeight = genislik / bitmapOran
            yukseklik = scaledHeight.toInt()
        } else {     //küçükse görsel dikey
            yukseklik = maximumSize
            val scaledWidth = yukseklik * bitmapOran
            genislik = scaledWidth.toInt()
        }
        return Bitmap.createScaledBitmap(image,genislik,yukseklik,true)
    }


    fun selectImage(view:View){
          //-------------------izin verildi mi onu döndürüyor------------------------------------  //o da böyle dönüyor; Permission_denıed olsaydı reddedildi demek.(denied=reddedildi)
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {//yani bu kod bloğunda izin verilmediyse olacaklar yazıyor.
                //android buna kendisi karar veriyor;"izin alma mantığını kullanıcıya göstereyim mi?"
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Snackbar.make(view, "Permission needed for gallery", Snackbar.LENGTH_INDEFINITE).setAction("Give Permission",
                    View.OnClickListener {
                        izinBaslatici.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                    }).show()
            } else {
                izinBaslatici.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        } else {  //izin verildiyse direkt galeriye gidiyoruz.                   //seçilen görselin telefonda nerede olduğu
            val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)  //intent le sadece aktivite değiştirmiyoruz, şuraya gidicez bir veri alacağızda diyebiliyoruz.
             activityResultLauncher.launch(intentToGallery)
        }
    }


                //başlatıcıları kayıt et.
    private fun registerLauncher() {
                    //intent koyduğumuz için istenen parametreler ona göre olacak.
        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { sonuc ->
            //Buradaki işlemler galeriye gitmek ve galeriden görseli seçmekle alakalı.
            //StartActivityForResult() başlatıldığında sonucunda ne olacağını yazdığımız kod bloğu.
                 //Kullanıcı doğru düzgün gitti mi?
            if (sonuc.resultCode == RESULT_OK) {
                val intentFromResult = sonuc.data       //get intent gibi, veriyi alıyor ama nullable veriyor çünkü veri oladabilir olmayada bilir.
                if (intentFromResult != null) {         //bize data döndürdüyse yapacaklarımızın kod bloğu. (fotoğrafı seçtiyse)
                    val imageData = intentFromResult.data   //datamızı imageData ya atadık.
                    //alttaki kod bloğunu yazmadan binding.imageView.setImageUri(imageData) yaparsak fotoğrafı alıp kullanıcıya gösterebilirsin ama sqlite a kaydetmek için bitmap kullanıp küçültmen gerek.
                    try {  //URI yı bitmap e çevireceğiz.
                        if (Build.VERSION.SDK_INT >= 28) {
                            val source = ImageDecoder.createSource(this.contentResolver, imageData!!)   //Urı ı görsel yapıyoruz
                            selectedBitmap = ImageDecoder.decodeBitmap(source)
                            binding.imageView.setImageBitmap(selectedBitmap)
                        } else {
                            selectedBitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, imageData)
                            binding.imageView.setImageBitmap(selectedBitmap)
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        }

        izinBaslatici = registerForActivityResult(ActivityResultContracts.RequestPermission()) { sonuc ->
            if (sonuc) {
                //permission granted
                val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)
            } else {
                //permission denied
                Toast.makeText(this, "Permisson needed!", Toast.LENGTH_LONG).show()
            }
        }
    }

}