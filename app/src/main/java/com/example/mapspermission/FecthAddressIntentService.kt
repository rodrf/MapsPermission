package com.example.mapspermission

import android.app.IntentService
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.os.ResultReceiver
import android.util.Log
import java.io.IOException
import java.lang.IllegalArgumentException
import java.util.*

const val LOCATION_DATA_EXTRA = "Location.LOCATION_DATA_EXTRA"
const val RESULT_DATA_KEY = "Location.LOCATION.DATA_KEY"
const val SUCCCESS_DATA_RESULT:Int =2001
const val FAILURE_DATA_RESULT:Int=2002
const val RECEIVER_LOCATION: String="Location.Service.RECEIVER"

class FecthAddressIntentService:IntentService("FechtAddressIntentService"){

    private var receiver:ResultReceiver?=null



    override fun onHandleIntent(intent: Intent?) {
        val geocoder = Geocoder(this, Locale.getDefault())
        var errorMessage=""

        val location: Location?= intent?.getParcelableExtra(LOCATION_DATA_EXTRA)
        receiver = intent?.getParcelableExtra(RECEIVER_LOCATION)
        var address:List<Address> = emptyList()

        try {


            if(location!=null){
                address = geocoder.getFromLocation(location.latitude,location.longitude,1)
                Log.e("addressList", "------>>>>${address?.toString()}")
            }
        }
        catch (ioException: IOException){
            ioException.printStackTrace()
            errorMessage = "Servicio no disponible"
        }catch (e: IllegalArgumentException){
            e.printStackTrace()
            errorMessage="LatLng es invalido, intenta de nuevo"
        }
        if (address.isNotEmpty()){
            val singleAddress = address[0]
            val addressFragments = with(singleAddress){
                (0..maxAddressLineIndex).map {
                    getAddressLine(it)
                }
            }
            val singleAddressLineJoined = addressFragments.joinToString(separator = "\n")
            deliveryResultToReceiver(SUCCCESS_DATA_RESULT, singleAddressLineJoined)




        }else{
            if(errorMessage.isEmpty()){
                errorMessage="Sin direccion disponible"
                deliveryResultToReceiver(FAILURE_DATA_RESULT,errorMessage)
            }
        }


    }
    private fun deliveryResultToReceiver(resultCode:Int, message:String){
        val bundle = Bundle().apply {
            putString(RESULT_DATA_KEY,message)
        }
        receiver?.send(resultCode,bundle)
    }


}