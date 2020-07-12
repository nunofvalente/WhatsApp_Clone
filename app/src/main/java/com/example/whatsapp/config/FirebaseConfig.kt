package com.example.whatsapp.config

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class FirebaseConfig {

    companion object {

        fun getFirebaseAuth(): FirebaseAuth {
            return FirebaseAuth.getInstance()
        }

        fun getFirebaseDb(): DatabaseReference {
            return FirebaseDatabase.getInstance().reference
        }

        fun getFirebaseStorage(): StorageReference {
            return FirebaseStorage.getInstance().reference
        }
    }
}