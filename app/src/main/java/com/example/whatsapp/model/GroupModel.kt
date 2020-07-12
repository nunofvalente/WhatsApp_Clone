package com.example.whatsapp.model

import android.os.Parcelable
import com.example.whatsapp.config.FirebaseConfig
import com.example.whatsapp.constants.Constants
import com.example.whatsapp.helper.Base64Custom
import kotlinx.android.parcel.Parcelize

@Parcelize
data class GroupModel(var idGroup: String, var nameGroup: String, var photo: String, var membersList: MutableList<UserModel>) :
    Parcelable {

    constructor(): this("","","", arrayListOf())

    fun save() {
        val database = FirebaseConfig.getFirebaseDb()
        val groupRef = database.child(Constants.DB.GROUP)

        val idFirebase = groupRef.push().key
        idGroup = idFirebase!!

        groupRef.child(idGroup).setValue(this)

        //Save chat for group members
        for(member in membersList) {

            val senderId = Base64Custom.encodeString(member.email!!)
            val receiverId = idGroup

            val chat = ChatModel()
            chat.isItGroup = "true"
            chat.group = this
            chat.senderId = senderId
            chat.receiverId = receiverId

            chat.save()
        }

    }
}