package com.example.whatsapp.model

import android.os.Parcelable
import com.example.whatsapp.config.FirebaseConfig
import com.example.whatsapp.constants.Constants
import com.example.whatsapp.helper.UserFirebase
import com.google.firebase.database.Exclude
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserModel(@get:Exclude var id: String?, var name: String?, var email: String?, @get:Exclude var password: String?, var photo: String?): Parcelable {

    constructor(): this("","","","","")

    private val firebase = FirebaseConfig.getFirebaseDb()

    fun updateUser() {
        val userId = UserFirebase.getEncodedEmail()
        val path = firebase.child(Constants.DB.USERS).child(userId)

        val userValues = convertToMap()

        path.updateChildren(userValues)

    }

    @Exclude
    fun convertToMap(): Map<String, Any> {
        val userMap = HashMap<String, Any>()

        userMap.put("email", email!!)
        userMap.put("name", name!!)
        userMap.put("photo", photo!!)

        return userMap
    }
}
