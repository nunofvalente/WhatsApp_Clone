package com.example.whatsapp.helper

import android.net.Uri
import android.util.Log
import com.example.whatsapp.config.FirebaseConfig
import com.example.whatsapp.model.UserModel
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import java.lang.Exception

class UserFirebase {

    companion object {

        private val auth = FirebaseConfig.getFirebaseAuth()
        private val firebase = FirebaseConfig.getFirebaseDb()

        fun getEncodedEmail(): String {

            val userEmail = auth.currentUser?.email

            return Base64Custom.encodeString(userEmail!!)
        }

        fun getCurrentUser(): FirebaseUser {
            return auth.currentUser!!
        }

        fun updateUserPhoto(url: Uri?): Boolean {

            try {
                val user = getCurrentUser()
                val profile = UserProfileChangeRequest.Builder().setPhotoUri(url).build()


                user.updateProfile(profile).addOnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        Log.d("Profile", "Error updating the profile photo")
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                return false
            }
            return true
        }

        fun updateUserName(name: String): Boolean {

            try {
                val user = getCurrentUser()
                val profile = UserProfileChangeRequest.Builder().setDisplayName(name).build()


                user.updateProfile(profile).addOnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        Log.d("Profile", "Error updating the profile name")
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                return false
            }
            return true
        }

        fun getLoggedUserData(): UserModel  {
            val firebaseUser = getCurrentUser()

            val user = UserModel("", "", "", "", "")
            user.email = firebaseUser.email.toString()
            user.name = firebaseUser.displayName.toString()

            if(firebaseUser.photoUrl == null) {
                user.photo = ""
            } else {
                user.photo = firebaseUser.photoUrl.toString()
            }

            return user
        }

    }

}
