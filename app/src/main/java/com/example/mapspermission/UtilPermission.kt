package com.example.mapspermission

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat

fun Context.hasPremission(permission:String): Boolean{
    return ContextCompat.checkSelfPermission(this,permission) == PackageManager.PERMISSION_GRANTED
}
fun Context.permissionToRequest(wantedPermission:Array<String>): List<String>{
    val resultPermission = ArrayList<String>()

    for(permission in wantedPermission){
        if (this.hasPremission(permission)){
            resultPermission.add(permission)
        }
    }
    return resultPermission
}