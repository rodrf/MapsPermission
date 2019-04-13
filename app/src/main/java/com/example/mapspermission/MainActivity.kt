package com.example.mapspermission

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog

class MainActivity : AppCompatActivity() {

    lateinit var locationManager: LocationManager
    var alertDialog:AlertDialog?=null
    var alertDialogTryAgain:AlertDialog?=null
    private val REQUEST_LOCATION_CODE: Int = 100

    internal var permissionList = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION

    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        val permissionListToREquest = permissionToRequest(permissionList)
        if(permissionListToREquest.isNotEmpty()){
            askForPermision(permissionListToREquest)
        }else{
            enableLocation()
        }
    }

    private fun askForPermision(permissionListToREquest: List<String>) {
        alertDialog = AlertDialog.Builder(this@MainActivity)
            .setTitle(getString(R.string.alert_dialog_location_title))
            .setMessage(getString(R.string.alert_dialog_location_message))
            .setCancelable(false)
            .setPositiveButton(getString(R.string.alert_diaog_accept)
            ) { dialog, _ ->
                dialog.dismiss()
                requestLocationPermission(permissionListToREquest)

            }
            .setNegativeButton(getString(R.string.alert_dialog_location_cancel)){dialog, _ ->
                dialog.dismiss()
            }
            .create()
        alertDialog?.show()
    }



    private fun requestLocationPermission(permissionListToREquest: List<String>) {
        requestPermissions(permissionListToREquest.toTypedArray(), REQUEST_LOCATION_CODE)
    }

    @SuppressLint("MissingPermission")
    private fun enableLocation(){
        val bestProvider = locationManager.getBestProvider(Criteria(), true) //TODO: debe ser true
        Log.i("BestProvider", "---->$bestProvider")
        val lastKnowLocation = locationManager.getLastKnownLocation(bestProvider)
        Log.e("LASTLOCATION", "$lastKnowLocation")

        locationManager.requestLocationUpdates(bestProvider
            , 5000,
            10f,
            object : LocationListener{
            override fun onLocationChanged(location: Location?) {
                Log.e("locationUpdate", "${location?.toString()}")
            }

            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
                Log.e("provider", "$provider status $status")
            }

            override fun onProviderEnabled(provider: String?) {
                Log.e("onProviderEnabled", "$provider")
            }

            override fun onProviderDisabled(provider: String?) {
                Log.e("onProviderDisabled", "$provider")
            }
        })

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Log.d("onRequestPermissions", "${permissions.map { it }}")
        Log.d("onRequestPermissions", "${grantResults.map { it }}")
        if (requestCode == REQUEST_LOCATION_CODE){
            val permissionBooleansResult :List<Boolean> = grantResults.map {
                it == PackageManager.PERMISSION_GRANTED
            }
            val permissionGranted:Boolean = permissionBooleansResult.firstOrNull() ?:false
            if(permissionGranted){
                enableLocation()

            }else{
                tryPermissionAgain(permissions)
            }
        }
    }

    private fun tryPermissionAgain(permissions: Array<out String>) {
        if(permissions.isNotEmpty()){
            alertDialogTryAgain = AlertDialog.Builder(this@MainActivity)
                .setTitle(getString(R.string.alert_dialog_location_title))
                .setMessage(getString(R.string.alert_dialog_message_tryagain))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.alert_diaog_accept)
                ) { dialog, _ ->
                    dialog.dismiss()
                    requestLocationPermission(permissions.toList())

                }
                .setNegativeButton(getString(R.string.alert_dialog_location_cancel)){dialog, _ ->
                    dialog.dismiss()
                }
                .create()
            alertDialogTryAgain?.show()
        }else{
            Toast.makeText(this@MainActivity, "Algo ocrurrio, intenta neuvamente", Toast.LENGTH_LONG).show()
        }

    }

    override fun onDestroy() {
        alertDialog?.dismiss()
        alertDialogTryAgain?.dismiss()
        super.onDestroy()
    }
}
