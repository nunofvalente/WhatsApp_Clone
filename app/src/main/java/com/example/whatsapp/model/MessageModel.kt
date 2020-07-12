package com.example.whatsapp.model


data class MessageModel(var userId: String, var name: String, var message: String, var image: String) {

    constructor(): this("", "", "","")
}