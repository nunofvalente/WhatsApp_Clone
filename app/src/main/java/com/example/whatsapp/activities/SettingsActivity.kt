package com.example.whatsapp.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.example.whatsapp.R
import com.example.whatsapp.config.FirebaseConfig
import com.example.whatsapp.constants.Constants
import com.example.whatsapp.helper.Permission
import com.example.whatsapp.helper.UserFirebase
import com.example.whatsapp.model.UserModel
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.android.synthetic.main.toolbar_settings.*
import java.io.ByteArrayOutputStream
import java.lang.Exception

@Suppress("DEPRECATION")
class SettingsActivity : AppCompatActivity(), View.OnClickListener {

    private var firebase = FirebaseConfig.getFirebaseDb()
    private var permissionNeeded = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
    private var userEncodedEmail = UserFirebase.getEncodedEmail()
    private var user = UserFirebase.getCurrentUser()
    private lateinit var loggedUser: UserModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        loggedUser = UserFirebase.getLoggedUserData()

        //validar permissoes
        Permission.validatePermissions(permissionNeeded, this, 1)

        /* Set Toolbar */
        setSupportActionBar(toolbar_settings)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        //Recover user data
        recoverUserData()

        setListeners()
    }

    private fun recoverUserData() {
        val url = user.photoUrl

        if(url != null) {
            Glide.with(this).load(url).into(circleImageContacts)
        } else {
            circleImageContacts.setImageResource(R.drawable.padrao)
        }

        editSettingsName.setText(user.displayName)
    }

    private fun setListeners() {
        imageOpenCamera.setOnClickListener(this)
        imageOpenPhotos.setOnClickListener(this)
        imageSaveName.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.imageOpenCamera -> {
                openCamera()
            }
            R.id.imageOpenPhotos -> {
                openGallery()
            }
            R.id.imageSaveName -> {
                saveUserDisplayName()
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        for (permissionResult in grantResults) {
            if (permissionResult == PackageManager.PERMISSION_DENIED) {
                alertValidatePermission()
            }
        }
    }

    private fun alertValidatePermission() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Permissions Denied")
        builder.setMessage("Permissions are required to use the app.")
        builder.setCancelable(false)
        builder.setPositiveButton("Confirm") { _, _ ->
            finish()
        }
        val dialog = builder.create()
        dialog.show()

    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        if (intent.resolveActivity(packageManager) != null) {
            startActivityForResult(intent, Constants.REQUEST_CODE.GALLERY_SELECTION)
        }
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(packageManager) != null) {
            startActivityForResult(intent, Constants.REQUEST_CODE.CAMERA_SELECTION)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            var image: Bitmap? = null

            try {
                when (requestCode) {
                    Constants.REQUEST_CODE.CAMERA_SELECTION -> {
                        image = data?.extras?.get("data") as Bitmap
                    }
                    Constants.REQUEST_CODE.GALLERY_SELECTION -> {
                        val imagePath = data?.data
                        when {
                            Build.VERSION.SDK_INT < 28 -> {
                                image = MediaStore.Images.Media.getBitmap(contentResolver, imagePath)
                            }
                            Build.VERSION.SDK_INT >= 28 -> {
                                image = ImageDecoder.decodeBitmap(ImageDecoder.createSource(contentResolver, imagePath!!))
                            }
                        }
                    }
                }
                if (image != null) {

                    circleImageContacts.setImageBitmap(image)

                    //Recover image data for firebase
                    val baos = ByteArrayOutputStream()
                    image.compress(Bitmap.CompressFormat.JPEG, 70, baos)
                    val imageData = baos.toByteArray()

                    //Save image in firebase
                    val storage = FirebaseConfig.getFirebaseStorage()
                    val imageRef = storage
                        .child(Constants.STORAGE.IMAGES)
                        .child(Constants.STORAGE.PROFILE_PIC)
                        .child(userEncodedEmail)
                        .child("perfil.jpeg")

                    val uploadTask = imageRef.putBytes(imageData)
                        uploadTask.addOnFailureListener(this) { Toast.makeText(this, "Error uploading image!", Toast.LENGTH_SHORT).show()}
                        .addOnSuccessListener {taskSnapshot ->
                            Toast.makeText(this, "Success uploading image!", Toast.LENGTH_SHORT).show()

                            val uri = taskSnapshot.metadata?.reference?.downloadUrl
                            while ((!uri?.isComplete!!));
                                val url = uri.result
                                updateUserImage(url)
                        }
                }

    } catch (e: Exception)
    {
        e.printStackTrace()
    }
}
}

    private fun updateUserImage(url: Uri?) {
        val updated = UserFirebase.updateUserPhoto(url)
        if(updated) {
            loggedUser.photo = url.toString()
            loggedUser.updateUser()
            Toast.makeText(this, "Photo Updated!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveUserDisplayName() {
        val name = editSettingsName.text.toString()

        val displayName = UserFirebase.updateUserName(name)
        if (displayName) {

            loggedUser.name = name
            loggedUser.updateUser()

            Toast.makeText(this, "Name change with success!", Toast.LENGTH_SHORT).show()
        }
    }

}
