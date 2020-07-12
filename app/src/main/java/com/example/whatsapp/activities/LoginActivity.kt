package com.example.whatsapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.whatsapp.R
import com.example.whatsapp.business.UserBusiness
import com.example.whatsapp.config.FirebaseConfig
import com.example.whatsapp.model.UserModel
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var user: UserBusiness

    private val auth = FirebaseConfig.getFirebaseAuth()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        user = UserBusiness(this)


        setListeners()
    }

    private fun setListeners() {
        textLoginRegister.setOnClickListener(this)
        buttonLogin.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when(view?.id) {
            R.id.textLoginRegister -> {
                startActivity(Intent(this, RegisterActivity::class.java))
                finish()
            }
            R.id.buttonLogin -> {
                loginUser()
            }
        }
    }

    private fun loginUser() {
        val email = editLoginEmail.text.toString()
        val password = editLoginPassword.text.toString()


        if(email.isNotEmpty()) {
            if(password.isNotEmpty()) {

                    val userModel = UserModel("", "", email, password, "")
                    user.loginUser(userModel)

                    Thread.sleep(1000)

                    startActivity(Intent(this, MainActivity::class.java))
                    finish()

            } else (Toast.makeText(this, "Please fill password!", Toast.LENGTH_SHORT).show())
        } else (Toast.makeText(this, "Please fill email address!", Toast.LENGTH_SHORT).show())

    }

    private fun verifyUser() {
        if (auth.currentUser != null) {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    override fun onStart() {
        super.onStart()
        verifyUser()
    }
}
