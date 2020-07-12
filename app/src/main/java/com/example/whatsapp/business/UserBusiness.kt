package com.example.whatsapp.business

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.example.whatsapp.activities.LoginActivity
import com.example.whatsapp.activities.MainActivity
import com.example.whatsapp.config.FirebaseConfig
import com.example.whatsapp.constants.Constants
import com.example.whatsapp.helper.Base64Custom
import com.example.whatsapp.helper.UserFirebase
import com.example.whatsapp.model.UserModel
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import java.lang.Exception
import java.util.*
import kotlin.collections.HashMap

class UserBusiness(var context: Context) {

    private var firebase = FirebaseConfig.getFirebaseDb()
    private var auth = FirebaseConfig.getFirebaseAuth()

    fun registerUser(userModel: UserModel) {

        auth.createUserWithEmailAndPassword(userModel.email!!, userModel.password!!)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(context, "User registered with success!", Toast.LENGTH_SHORT).show()
                    UserFirebase.updateUserName(userModel.name!!)

                    startActivity(Intent(LoginActivity(), MainActivity::class.java))
                } else {

                    val exception: String
                    try {
                        throw task.exception!!
                    } catch (e: FirebaseAuthWeakPasswordException) {
                        exception = "Please enter a stronger password!"
                    } catch (f: FirebaseAuthInvalidCredentialsException) {
                        exception = "Please enter a valid email!"
                    } catch (g: FirebaseAuthUserCollisionException) {
                        exception = "Account already registered!"
                    } catch (e: Exception) {
                        exception = "Error registering user: " + e.message
                        e.printStackTrace()
                    }
                    Toast.makeText(context, exception, Toast.LENGTH_SHORT).show()
                }

            }
    }

    private fun startActivity(intent: Intent) {

    }

    fun loginUser(userModel: UserModel) {

        auth.signInWithEmailAndPassword(userModel.email!!, userModel.password!!)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.i("Sign In", "User successful signed in!")
                } else {

                    val exception: String
                    try {
                        throw task.exception!!
                    } catch (e: FirebaseAuthInvalidUserException) {
                        exception = "Email address does not exist!"
                    } catch (e: FirebaseAuthInvalidCredentialsException) {
                        exception = "Password does not match with email!"
                    } catch (e: Exception) {
                        exception = "Error logging in: " + e.message
                        e.printStackTrace()
                    }

                    Toast.makeText(context, exception, Toast.LENGTH_SHORT).show()
                }
            }
    }

    fun saveUser(userModel: UserModel) {

        val path = firebase.child(Constants.DB.USERS)
        val email = Base64Custom.encodeString(userModel.email!!)

        path.child(email).setValue(userModel)
    }


}

