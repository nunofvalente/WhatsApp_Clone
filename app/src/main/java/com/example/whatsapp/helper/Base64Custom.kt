package com.example.whatsapp.helper

import android.util.Base64

class Base64Custom {

    companion object {
        fun encodeString(text: String): String {
            return Base64.encodeToString(text.toByteArray(), Base64.NO_WRAP)
                .replace("(\\n|\\r)", "")
        }

        fun decodeString(text: String): String {
            return Base64.decode(text.toByteArray(), Base64.NO_WRAP).toString()
        }
    }
}