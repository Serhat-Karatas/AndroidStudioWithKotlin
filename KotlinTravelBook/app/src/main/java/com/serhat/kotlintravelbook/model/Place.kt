package com.serhat.kotlintravelbook.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity                     //burası tablo adımız gibi istersek @Entity(tablo adı)şeklinde kullanabiliriz
class Place(                 //bu haliyle tablo adımız Place
    @ColumnInfo(name="name")    //kolon isimlerimizi veriyoruz.
    var name:String,

    @ColumnInfo(name="latitude")   //Tırnak içine değişkenimizle aynı şeyi yazmak zorunda değiliz.Anlaşılır olsun diye böyle
    var latitude:Double,

    @ColumnInfo(name="longitude")
    var longitude:Double
    ) {

    @PrimaryKey(autoGenerate = true)             //otomatik id atama
    var id=0
}