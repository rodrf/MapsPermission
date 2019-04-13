package com.example.mapspermission

import android.annotation.SuppressLint
import android.content.Intent
import android.location.Geocoder
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.core.content.res.FontResourcesParserCompat
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.*
import kotlinx.android.synthetic.main.activity_gpslocation.*

class GPSLocationActivity : AppCompatActivity(), GoogleApiClient.OnConnectionFailedListener,
    GoogleApiClient.ConnectionCallbacks {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    lateinit var googleApiClient: GoogleApiClient
    private var locationRequest: LocationRequest? = null
    private lateinit var locationCallback: LocationCallback
    lateinit var resultAddressREceiver:AddressResultReceiver



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gpslocation)
        resultAddressREceiver = AddressResultReceiver(Handler())

        resultAddressREceiver.setOnFetchAddressListener { address->
            Log.d("AddresGeocoder", address)
        }



        googleApiClient = GoogleApiClient.Builder(this@GPSLocationActivity)
            .enableAutoManage(this@GPSLocationActivity, this)
            .addConnectionCallbacks(this)
            .addApi(LocationServices.API)
            .build()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this@GPSLocationActivity)

        locationCallback = object : LocationCallback(){
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.locations.map { location ->
                    Log.e("LocationUpdates", "${location?.toString()}")
                }

                super.onLocationResult(locationResult)
            }
        }

        /*btnRod?.setOnClickListener{fusedLocationClient.lastLocation?.addOnSuccessListener { lastLoc ->
            if(!Geocoder.isPresent()){
                Toast.makeText(this@GPSLocationActivity,"no dis", Toast.LENGTH_LONG).show()
            }
        })
        }*/





        Log.d("OnCreate","jejejej")



    }
    private fun startIntentService(lastKnowLocat: Location?){
        val intent = Intent(this@GPSLocationActivity, FecthAddressIntentService::class.java).apply {
            putExtra(RECEIVER_LOCATION, resultAddressREceiver)
            putExtra(LOCATION_DATA_EXTRA, lastKnowLocat)
        }
        startService(intent)
    }

    @SuppressLint("MissingPermission")
    override fun onResume() {
        super.onResume()
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
    }

    override fun onStop() {
        super.onStop()
        fusedLocationClient.removeLocationUpdates(locationCallback)
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
        Log.e("Conectado", "Si se conecto")
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
        locationRequest = createLocationRequest()

        val locationSettingsRequest = locationRequest?.let { locationRq ->
            LocationSettingsRequest.Builder()
                .addLocationRequest(locationRq)
                .build()
        }

        val clientLocation = LocationServices.getSettingsClient(this@GPSLocationActivity)
        clientLocation.checkLocationSettings(locationSettingsRequest)

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)



    }
    private fun createLocationRequest() = LocationRequest.create().apply {
        interval = 1000L
        fastestInterval = 5000
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY


    }


}
