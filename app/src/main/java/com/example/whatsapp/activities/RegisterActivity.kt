package com.example.whatsapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.whatsapp.R
import com.example.whatsapp.business.UserBusiness
import com.example.whatsapp.model.UserModel
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var user: UserBusiness

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        user = UserBusiness(this)

        setListeners()
    }

    private fun setListeners() {
        buttonRegister.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when(view?.id) {
            R.id.buttonRegister -> {
                registerUser()
            }
        }
    }

    private fun registerUser() {

        val name = editRegisterName.text.toString()
        val email = editRegisterEmail.text.toString()
        val password = editRegisterPassword.text.toString()
        val confirmPassword = editRegisterConfirmPassword.text.toString()

        if(name.isNotEmpty()) {
            if(email.isNotEmpty()) {
                if(password.isNotEmpty() && password.count() >= 6) {
                    if(password == confirmPassword) {

                        val userModel = UserModel("", name, email, password, "")
                        user.registerUser(userModel)
                        user.saveUser(userModel)

                    } else {
                        Toast.makeText(this, "Passwords must match!", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Password must contain at least 6 characters!", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please fill your email!", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Please fill your name!", Toast.LENGTH_SHORT).show()
        }
    }
}
