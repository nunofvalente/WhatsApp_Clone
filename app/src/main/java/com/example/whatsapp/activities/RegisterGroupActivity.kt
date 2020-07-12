package com.example.whatsapp.activities

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.UserHandle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.whatsapp.R
import com.example.whatsapp.adapter.GroupAdapter
import com.example.whatsapp.config.FirebaseConfig
import com.example.whatsapp.constants.Constants
import com.example.whatsapp.helper.UserFirebase
import com.example.whatsapp.model.GroupModel
import com.example.whatsapp.model.UserModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import kotlinx.android.synthetic.main.activity_group_activity.*

import kotlinx.android.synthetic.main.activity_register_group.*
import kotlinx.android.synthetic.main.activity_register_group.toolbar
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.android.synthetic.main.content_register_group.*
import java.io.ByteArrayOutputStream
import java.lang.Exception

class RegisterGroupActivity : AppCompatActivity(), View.OnClickListener {

    private val mGroupUserList: MutableList<UserModel> = arrayListOf()

    private lateinit var group: GroupModel
    private lateinit var mRecyclerParticipants: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_group)
        setSupportActionBar(toolbar)

        supportActionBar?.title = "New Group"
        supportActionBar?.subtitle = "Add group name"

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        mRecyclerParticipants = findViewById(R.id.recyclerParticipants)
        group = GroupModel()

        recoverDataFromActivity()
        loadRecyclerParticipants()
        setListeners()
    }

    private fun loadRecyclerParticipants() {

        mRecyclerParticipants.adapter = GroupAdapter(mGroupUserList, this)

        mRecyclerParticipants.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        mRecyclerParticipants.setHasFixedSize(true)
    }

    private fun recoverDataFromActivity() {
        if (intent.extras != null) {
            val list = intent.extras?.getSerializable("members") as MutableCollection<UserModel>
            mGroupUserList.addAll(list)

            textTotalParticipants.text = "Participants: ${mGroupUserList.size}"

        }
    }

    private fun setListeners() {
        circleImageGroup.setOnClickListener(this)
        fabGroupRegister.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.fabGroupRegister -> {
                saveGroup()
                Toast.makeText(this, "Group created with success", Toast.LENGTH_SHORT).show()

            }

            R.id.circleImageGroup -> loadGroupImage()
        }
    }

    private fun saveGroup() {

       val nameGroup = editGroupName.text.toString()

        //add to the selected members list the logged user
        mGroupUserList.add(UserFirebase.getLoggedUserData())
        group.membersList = mGroupUserList
        group.nameGroup = nameGroup
        group.save()

        val intent = Intent(this, ChatActivity::class.java)
        intent.putExtra("chatGroup", group)
        startActivity(intent)
    }

    private fun loadGroupImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        if (intent.resolveActivity(packageManager) != null) {
            startActivityForResult(intent, Constants.REQUEST_CODE.GALLERY_SELECTION)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            var image: Bitmap? = null

            try {
                when(requestCode) {
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

                    circleImageGroup.setImageBitmap(image)

                    //Recover image data for firebase
                    val baos = ByteArrayOutputStream()
                    image.compress(Bitmap.CompressFormat.JPEG, 70, baos)
                    val imageData = baos.toByteArray()

                    //Save image in firebase

                    val storage = FirebaseConfig.getFirebaseStorage()
                    val imageRef = storage
                        .child(Constants.STORAGE.IMAGES)
                        .child(Constants.STORAGE.GROUP)
                        .child("group.jpeg")

                    val uploadTask = imageRef.putBytes(imageData)
                    uploadTask.addOnFailureListener(this) { Toast.makeText(this, "Error uploading image!", Toast.LENGTH_SHORT).show()}
                        .addOnSuccessListener {taskSnapshot ->
                            Toast.makeText(this, "Success uploading image!", Toast.LENGTH_SHORT).show()

                            imageRef.downloadUrl.addOnCompleteListener {task ->
                                val url = task.result.toString()
                                group.photo = url
                            }
                        }

                }

            } catch (e: Exception)
            {
                e.printStackTrace()
            }
        }
    }
}

