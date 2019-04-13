package com.example.mapspermission

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class GPSLocationActivity : AppCompatActivity(), GoogleApiClient.OnConnectionFailedListener,
    GoogleApiClient.ConnectionCallbacks {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    lateinit var googleApiClient: GoogleApiClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gpslocation)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this@GPSLocationActivity)
        googleApiClient = GoogleApiClient.Builder(this@GPSLocationActivity)
            .enableAutoManage(this@GPSLocationActivity, this)
            .addConnectionCallbacks(this)
            .addApi(LocationServices.API)
            .build()
        Log.d("OnCreate","jejejej")

    }

    override fun onStart() {

        super.onStart()
        if(::googleApiClient.isInitialized){
            googleApiClient.connect()
        }
    }

    override fun onPause() {
        super.onPause()
        if(::googleApiClient.isInitialized && googleApiClient.isConnected){
            googleApiClient.disconnect()
        }
    }

    override fun onConnected(p0: Bundle?) {
        enableLocation()
    }


    override fun onConnectionSuspended(p0: Int) {

    }

    override fun onConnectionFailed(p0: ConnectionResult) {

    }
    @SuppressLint("MissingPermission")
    private fun enableLocation() {
        fusedLocationClient.lastLocation?.addOnSuccessListener { location ->
            Log.d("LastLocation", "----->${location?.toString()}")

        }
    }


}
