package com.example.whatsapp.helper

import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class Permission {

    companion object {

        fun validatePermissions(permissions: Array<String>, activity: Activity, requestCode: Int): Boolean {
            if(Build.VERSION.SDK_INT >= 23) {

                val listPermissions = arrayListOf<String>()

                //Goes through the list to see if permissions were granted
                for(permission in permissions) {
                    val hasPermission = ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED
                    if( !hasPermission) {
                        listPermissions.add(permission)
                    }
                }

                //if list empty no need to ask for permission
                if(listPermissions.isEmpty()) {
                    return true
                }
                val newPermissions = arrayOfNulls<String>(listPermissions.size)
                listPermissions.toArray(newPermissions)

                //ask for permission
                ActivityCompat.requestPermissions(activity, newPermissions, requestCode )

            }

            return true
        }
    }
}