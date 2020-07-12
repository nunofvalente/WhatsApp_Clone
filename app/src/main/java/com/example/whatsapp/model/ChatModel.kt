package com.example.whatsapp.model

import android.os.Parcelable
import com.example.whatsapp.config.FirebaseConfig
import com.example.whatsapp.constants.Constants
import kotlinx.android.parcel.Parcelize

data class ChatModel(var senderId: String, var receiverId: String, var lastMessage: String, var isItGroup: String, var userShown: UserModel, var group: GroupModel) {


    constructor(): this("","","", "", UserModel(), GroupModel()) {
        isItGroup = "false"
    }

    fun save() {
        val database = FirebaseConfig.getFirebaseDb()
        val chatRef = database.child(Constants.DB.CHAT)

        chatRef.child(senderId).child(receiverId).setValue(this)
    }
}