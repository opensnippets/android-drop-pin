package com.example.simplegps

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity(), LocationListener {
    private lateinit var locationManager: LocationManager
    private lateinit var tvGpsLon: TextView
    private lateinit var tvGpsLat: TextView
    private val locationPermissionCode = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        title = "Location Drop Point"
        locationSetup()
        val button: Button = findViewById(R.id.buttonGetGPS)
        val lon = findViewById<TextView>(R.id.textViewLon)
        val lat = findViewById<TextView>(R.id.textViewLat)
        lat.text = ""
        lon.text = ""
        button.setOnClickListener {
            //process the google map url
            //see documentation: https://developers.google.com/maps/documentation/urls/android-intents
            if (lon.text.isNotEmpty() and lat.text.isNotEmpty()) {
                Log.d(MainActivity::class.java.name, "button is click!")
                var uriStr = "geo:${lat.text},${lon.text}?z=20"
                val gmmIntentUri = Uri.parse(uriStr)
                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)

                mapIntent.setPackage("com.google.android.apps.maps")
                mapIntent.resolveActivity(packageManager)?.let {
                    Log.d(MainActivity::class.java.name, "map Intent: $uriStr")
                    startActivity(mapIntent)
                }
            } else {
                Toast.makeText(this, "There is no valid location!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun locationSetup() {
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                locationPermissionCode
            )
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5f, this)
        Log.d(MainActivity::class.java.name, "getLocation")
    }

    override fun onLocationChanged(location: Location) {
        tvGpsLon = findViewById(R.id.textViewLon)
        tvGpsLat = findViewById(R.id.textViewLat)
        tvGpsLon.text = location.longitude.toString()
        tvGpsLat.text = location.latitude.toString()
        Log.d(MainActivity::class.java.name, "onLocationChanged")
        Log.d(MainActivity::class.java.name, "lat: ${location.latitude}")
        Log.d(MainActivity::class.java.name, "lon: ${location.longitude}")
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == locationPermissionCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Location Permission Granted", Toast.LENGTH_SHORT).show()
            }
            else {
                Toast.makeText(this, "Location Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    //This method was deprecated in API level 29
    override fun onStatusChanged(
            provider: String?,
            status: Int,
            extras: Bundle?
    ) {
        Log.d(MainActivity::class.java.name, "Provider: $provider - Status: $status")
    }
}