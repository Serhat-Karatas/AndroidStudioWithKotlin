package com.serhat.kotlintravelbook.view

import android.Manifest
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.room.Room
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import com.serhat.kotlintravelbook.R
import com.serhat.kotlintravelbook.databinding.ActivityMapsBinding
import com.serhat.kotlintravelbook.model.Place
import com.serhat.kotlintravelbook.roomdb.PlaceDao
import com.serhat.kotlintravelbook.roomdb.PlaceDatabase
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers

class MapsActivity : AppCompatActivity(), OnMapReadyCallback,GoogleMap.OnMapLongClickListener {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var locationManager: LocationManager
    private lateinit var locationListener: LocationListener
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    private val mDisposable = CompositeDisposable()
    var selectedLatitude: Double? = null
    var selectedLongitude: Double? = null
    var placeFromMain: Place? = null
    private lateinit var db: PlaceDatabase
    private lateinit var placeDao: PlaceDao
    private lateinit var sharedPreferences: SharedPreferences
    var trackBoolean: Boolean? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        registerLauncher()

        selectedLatitude = 0.0
        selectedLongitude = 0.0

        binding.saveButton.isEnabled = false

        sharedPreferences =
            getSharedPreferences("com.serhat.kotlintravelbook", MODE_PRIVATE)
        trackBoolean = false


        db = Room.databaseBuilder(
            applicationContext,
            PlaceDatabase::class.java, "Places"
        ) //.allowMainThreadQueries() ->ana thread de yapılacak işlemmlere izin ver demek
            .build()

        placeDao = db.placeDao()

    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setOnMapLongClickListener(this)

        val intent = intent
        val info = intent.getStringExtra("info")

        if (info == "new") {
            binding.saveButton.visibility = View.VISIBLE
            binding.deleteButton.visibility = View.GONE
                             //LocationManager döndürür.
            locationManager = this.getSystemService(LOCATION_SERVICE) as LocationManager       //konumla ilgili bütün işlemleri yapan arkadaş(LocationManager)
            locationListener = object : LocationListener {
                override fun onLocationChanged(location: Location) {
                    trackBoolean = sharedPreferences.getBoolean("trackBoolean", false)
                    if (!trackBoolean!!) {
                        val userLocation = LatLng(location.latitude, location.longitude)
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15f))
                        sharedPreferences.edit().putBoolean("trackBoolean", true).apply()
                    }
                }

            }
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                //request permission
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        this,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                ) {
                    Snackbar.make(
                        binding.root,
                        "Permission needed for location",
                        Snackbar.LENGTH_INDEFINITE
                    ).setAction("Give Permission") {
                        permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                    }.show()
                } else {
                    permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                }
            } else {
                locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    0,
                    0f,
                    locationListener
                )
                val lastLocation =
                    locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                if (lastLocation != null) {
                    val lastUserLocation = LatLng(lastLocation.latitude, lastLocation.longitude)
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastUserLocation, 15f))
                }
            }
        } else {
            //Sqlite data && intent data
            mMap.clear()
            placeFromMain = intent.getSerializableExtra("place") as? Place
            placeFromMain?.let {
                val latLng = LatLng(it.latitude, it.longitude)

                mMap.addMarker(MarkerOptions().position(latLng).title(it.name))
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))

                binding.placeNameText.setText(it.name)
                binding.saveButton.visibility = View.GONE
                binding.deleteButton.visibility = View.VISIBLE

            }

        }
    }
                               //haritada nereye tıklandığı (latLng)
    override fun onMapLongClick(latLng: LatLng) {
        mMap.clear()  //bunu yazmazsan her tıklayış için farklı markerlar oluşur.
        mMap.addMarker(MarkerOptions().position(latLng))
        selectedLatitude = latLng.latitude
        selectedLongitude = latLng.longitude
        binding.saveButton.isEnabled = true
    }

    private fun handleResponse() {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }

    fun save(view: View?) {
        //main veya UI thread de çok fazla işlem yapmak kullanıcı arayüzünü bloklayabilir
        //Default thread->CPU (10000 tane kelimeyi alfebetik olarak dizmek gibi büyük işler için kullanılan thred)
        //IO thread İnternet/Datebase işlemlerinde  RxJava
        //placeDao.insert(place).subscribeOn(Schedulers.io()).subscribe();
        val place = Place(
            binding.placeNameText.text.toString(),
            selectedLatitude!!, selectedLongitude!!
        )
        mDisposable.add(
            placeDao.insert(place)               //şimdi RxJava bize bu işlemi hangi thread de yapacağımızı soruyor.
                .subscribeOn(Schedulers.io())      //işlemin yapılacağı yer
                .observeOn(AndroidSchedulers.mainThread())        //işlemin gözlemleneceği yer
                .subscribe(this::handleResponse)
        )
    }

    fun delete(view: View?) {
        placeFromMain?.let {
            mDisposable.add(
                placeDao.delete(it)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::handleResponse)
            )

        }
    }

    private fun registerLauncher() {                                          //boolean döndürür. İzin verildi mi verilmedi mi
        permissionLauncher = registerForActivityResult(RequestPermission()) { result ->
            if (result) {
                //permission granted
                if (ContextCompat.checkSelfPermission(
                        this@MapsActivity,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        0,
                        0f,
                        locationListener
                    )
                    val lastLocation =
                        locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                    if (lastLocation != null) {
                        val lastUserLocation = LatLng(lastLocation.latitude, lastLocation.longitude)
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastUserLocation, 15f))
                    }
                }
            } else {
                //permission denied
                Toast.makeText(this@MapsActivity, "Permisson needed!", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mDisposable.clear()
    }

}
