package com.example.whatsapp.activities

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.UserManager
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.whatsapp.R
import com.example.whatsapp.adapter.MessagesAdapter
import com.example.whatsapp.config.FirebaseConfig
import com.example.whatsapp.constants.Constants
import com.example.whatsapp.helper.Base64Custom
import com.example.whatsapp.helper.RecyclerItemClickListener
import com.example.whatsapp.helper.UserFirebase
import com.example.whatsapp.model.ChatModel
import com.example.whatsapp.model.GroupModel
import com.example.whatsapp.model.MessageModel
import com.example.whatsapp.model.UserModel
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.StorageReference

import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.android.synthetic.main.content_chat.*
import kotlinx.android.synthetic.main.fragment_chats.*
import kotlinx.android.synthetic.main.message_adapter_sender.*
import java.io.ByteArrayOutputStream
import java.lang.Exception
import java.util.*

class ChatActivity : AppCompatActivity(), View.OnClickListener {

    //identify recipient and sender
    private lateinit var senderId: String
    private lateinit var receiverId: String
    private var mMessageList: MutableList<MessageModel> = arrayListOf()

    private lateinit var userLogged: UserModel
    private lateinit var group: GroupModel
    private lateinit var mUserReceiver: UserModel
    private lateinit var childEventListenerMessages: ChildEventListener
    private lateinit var database: DatabaseReference
    private lateinit var messagesRef: DatabaseReference
    private lateinit var storage: StorageReference
    private lateinit var mRecyclerMessages: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        setSupportActionBar(toolbarChat)

        supportActionBar?.title = ""
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        //recover data from sender
        mUserReceiver = UserModel()
        group = GroupModel()
        senderId = UserFirebase.getEncodedEmail()
        userLogged = UserFirebase.getLoggedUserData()

        //set Recycler View
        configureRecycler()

        storage = FirebaseConfig.getFirebaseStorage()


        setListeners()
        recoverReceiverData()
    }

    private fun configureRecycler() {
        mRecyclerMessages = findViewById(R.id.recyclerMessages)

        mRecyclerMessages.adapter = MessagesAdapter(mMessageList, this)

        mRecyclerMessages.layoutManager = LinearLayoutManager(this)
        mRecyclerMessages.setHasFixedSize(true)
    }

    private fun setListeners() {
        floatingSend.setOnClickListener(this)
        imagePhotoChat.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.floatingSend -> sendMessage()
            R.id.imagePhotoChat -> openCamera()
        }
    }

    private fun recoverReceiverData() {
        val bundle = intent.extras

        if (bundle != null) {

            if (bundle.containsKey("chatGroup")) {
                group = bundle.getParcelable<GroupModel>("chatGroup") as GroupModel
                receiverId = group.idGroup

                textUserNameChat.text = group.nameGroup

                val photo = group.photo

                if (photo != "") {
                    val url = Uri.parse(photo)
                    Glide.with(this)
                        .load(url)
                        .into(circleImagePhotoChat)
                } else {
                    circleImagePhotoChat.setImageResource(R.drawable.padrao)
                }

            } else {
                mUserReceiver = bundle.getParcelable<UserModel?>("chatContact") as UserModel
                textUserNameChat.text = mUserReceiver.name

                val photo = mUserReceiver.photo

                if (photo != null) {
                    val url = Uri.parse(photo)
                    Glide.with(this)
                        .load(url)
                        .into(circleImagePhotoChat)
                } else {
                    circleImagePhotoChat.setImageResource(R.drawable.padrao)
                }

                //recover data from receiver
                receiverId = Base64Custom.encodeString(mUserReceiver.email.toString())
            }
        }
    }

    private fun sendMessage() {
        val textMessage = editWriteMessage.text.toString()

        if (textMessage.isNotEmpty()) {

            if (mUserReceiver != UserModel("", "", "", "", "")) {
                val message = MessageModel("", "", "", "")
                message.userId = senderId
                message.message = textMessage

                //save message in Firebase for sender
                saveMessage(senderId, receiverId, message)
                //save message in Firebase for receiver
                saveMessage(receiverId, senderId, message)

                //save chat for sender
                saveChat(senderId, receiverId, mUserReceiver, message, false)

                //save chat for receiver
                saveChat(receiverId, senderId, userLogged, message, false)

            } else {
                for (member in group.membersList) {
                    val idGroupSender = Base64Custom.encodeString(member.email!!)
                    val idLoggedUserGroup = UserFirebase.getEncodedEmail()

                    val message = MessageModel()
                    message.userId = idLoggedUserGroup
                    message.message = textMessage
                    message.name = userLogged.name!!

                    //
                    saveMessage(idGroupSender, receiverId, message)

                    //
                    saveChat(idGroupSender, receiverId, mUserReceiver, message, true)
                }
            }


        } else {
            Toast.makeText(this, "Please write a message!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveMessage(sendId: String, receiveId: String, msg: MessageModel) {

        val database = FirebaseConfig.getFirebaseDb()
        val messageRef = database.child(Constants.DB.MESSAGES)

        messageRef.child(sendId).child(receiveId).push().setValue(msg)

        editWriteMessage.setText("")
    }

    private fun recoverMessagesList() {

        val userId = UserFirebase.getEncodedEmail()

        database = FirebaseConfig.getFirebaseDb()
        messagesRef = database.child(Constants.DB.MESSAGES).child(senderId).child(receiverId)

        mMessageList.clear()

        childEventListenerMessages = messagesRef.addChildEventListener(object : ChildEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onChildMoved(dataSnapshot: DataSnapshot, p1: String?) {
                TODO("Not yet implemented")
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, p1: String?) {
                TODO("Not yet implemented")
            }

            override fun onChildAdded(dataSnapshot: DataSnapshot, p1: String?) {
                val message = dataSnapshot.getValue(MessageModel::class.java)
                mMessageList.add(message!!)

                recyclerMessages.adapter?.notifyDataSetChanged()
            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                TODO("Not yet implemented")
            }
        })
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
                }

                if (image != null) {

                    //Recover image data for firebase
                    val baos = ByteArrayOutputStream()
                    image.compress(Bitmap.CompressFormat.JPEG, 70, baos)
                    val imageData = baos.toByteArray()

                    val imageName = UUID.randomUUID().toString()

                    val imageRef = storage
                        .child(Constants.STORAGE.IMAGES)
                        .child(Constants.STORAGE.CHAT_PHOTOS)
                        .child(senderId)
                        .child(imageName)

                    val uploadTask = imageRef.putBytes(imageData)
                    uploadTask.addOnFailureListener { Log.d("Error", "Error uploading image!") }
                        .addOnSuccessListener { taskSnapshot ->

                            val downloadUrl = taskSnapshot.metadata?.reference?.downloadUrl
                            while ((!downloadUrl?.isComplete!!));
                            val url = downloadUrl.result

                            if (mUserReceiver != UserModel("", "", "", "", "")) {

                                val message = MessageModel()
                                message.userId = senderId
                                message.message = "image.jpeg"
                                message.image = url.toString()

                                //save data for sender
                                saveMessage(receiverId, senderId, message)

                                //save data for receiver
                                saveMessage(senderId, receiverId, message)

                            } else {
                                for (member in group.membersList) {
                                    val idGroupSender = Base64Custom.encodeString(member.email!!)
                                    val idLoggedUserGroup = UserFirebase.getEncodedEmail()

                                    val message = MessageModel()
                                    message.userId = idLoggedUserGroup
                                    message.message = "image.jpeg"
                                    message.name = userLogged.name!!
                                    message.image = url.toString()

                                    //
                                    saveMessage(idGroupSender, receiverId, message)

                                    //
                                    saveChat(idGroupSender, receiverId, mUserReceiver, message, true)
                                }
                            }
                            Toast.makeText(this, "Image sent", Toast.LENGTH_SHORT).show()
                        }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }


    }

    private fun saveChat(idSender: String, idReceiver: String, shownUser: UserModel, msg: MessageModel, isGroup: Boolean) {

        val chatSender = ChatModel()

        chatSender.senderId = idSender
        chatSender.receiverId = idReceiver
        chatSender.lastMessage = msg.message

        if (isGroup) { //group chat
            chatSender.isItGroup = "true"
            chatSender.group = group

        } else { //normal chat

            chatSender.group.idGroup = ""
            chatSender.userShown = shownUser
            chatSender.isItGroup = "false"
        }

        chatSender.save()
    }

    override fun onStart() {
        super.onStart()
        recoverMessagesList()
    }

    override fun onStop() {
        super.onStop()
        messagesRef.removeEventListener(childEventListenerMessages)
    }
}
