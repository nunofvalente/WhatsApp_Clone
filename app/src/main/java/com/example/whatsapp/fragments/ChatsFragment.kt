package com.example.whatsapp.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.example.whatsapp.R
import com.example.whatsapp.activities.ChatActivity
import com.example.whatsapp.adapter.ChatAdapter
import com.example.whatsapp.config.FirebaseConfig
import com.example.whatsapp.constants.Constants
import com.example.whatsapp.helper.RecyclerItemClickListener
import com.example.whatsapp.helper.UserFirebase
import com.example.whatsapp.model.ChatModel
import com.example.whatsapp.model.MessageModel
import com.example.whatsapp.model.UserModel
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_chats.*


class ChatsFragment : Fragment() {

    private lateinit var mRecyclerChatTab: RecyclerView
    private lateinit var childEventListener: ChildEventListener
    private lateinit var userRef: DatabaseReference
    private var adapter: ChatAdapter = ChatAdapter(mutableListOf(), null)

    private var mChatList: MutableList<ChatModel> = arrayListOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_chats, container, false)

        mRecyclerChatTab = view.findViewById(R.id.recyclerChatTab)

        val userId = UserFirebase.getEncodedEmail()
        userRef = FirebaseConfig.getFirebaseDb().child(Constants.DB.CHAT).child(userId)

        loadRecyclerView()
        setListeners()

        return view
    }


    fun loadChat() {
        adapter = ChatAdapter(mChatList, activity)
        mRecyclerChatTab.adapter = adapter
        mRecyclerChatTab.adapter?.notifyDataSetChanged()
    }

    private fun loadRecyclerView() {

        adapter = ChatAdapter(mChatList, this.activity)

        mRecyclerChatTab.adapter = adapter
        mRecyclerChatTab.layoutManager = LinearLayoutManager(this.activity)
        mRecyclerChatTab.setHasFixedSize(true)
    }

    fun searchChat(text: String) {

        val list: MutableList<ChatModel> = arrayListOf()

        for (chat in mChatList) {
            if (chat.userShown != UserModel()) {
                val lowerText = text.toLowerCase()
                val name = chat.userShown.name?.toLowerCase()
                val lastMessage = chat.lastMessage?.toLowerCase()
                if (name!!.contains(lowerText) || lastMessage.contains(lowerText)) {
                    list.add(chat)
                }
            } else {
                val lowerText = text.toLowerCase()
                val name = chat.group.nameGroup.toLowerCase()
                val lastMessage = chat.lastMessage.toLowerCase()
                if (name!!.contains(lowerText) || lastMessage.contains(lowerText)) {
                    list.add(chat)
                }
            }

            adapter = ChatAdapter(list, activity)
            mRecyclerChatTab.adapter = adapter
            mRecyclerChatTab.adapter?.notifyDataSetChanged()
        }
    }

    private fun recoverUsers() {

        mChatList.clear()

        childEventListener = userRef.addChildEventListener(object : ChildEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
                TODO("Not yet implemented")
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, p1: String?) {
                TODO("Not yet implemented")
            }

            override fun onChildAdded(dataSnapshot: DataSnapshot, p1: String?) {

                val chat = dataSnapshot.getValue(ChatModel::class.java)
                mChatList.add(chat!!)

                recyclerChatTab.adapter?.notifyDataSetChanged()
            }

            override fun onChildRemoved(p0: DataSnapshot) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun setListeners() {
        mRecyclerChatTab.addOnItemTouchListener(
            RecyclerItemClickListener(activity, mRecyclerChatTab,
                object : RecyclerItemClickListener.OnItemClickListener {
                    override fun onItemClick(view: View?, position: Int) {

                        val updatedList = adapter.getList()

                        val chatSelected = updatedList[position]

                        if (chatSelected.isItGroup == "true") {
                            val intent = Intent(activity, ChatActivity::class.java)
                            intent.putExtra("chatGroup", chatSelected.group)
                            startActivity(intent)

                        } else {
                            val intent = Intent(activity, ChatActivity::class.java)
                            intent.putExtra("chatContact", chatSelected.userShown)
                            startActivity(intent)
                        }

                    }

                    override fun onItemClick(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        TODO("Not yet implemented")
                    }

                    override fun onLongItemClick(view: View?, position: Int) {
                        TODO("Not yet implemented")
                    }

                })
        )
    }

    override fun onStart() {
        super.onStart()
        recoverUsers()
    }

    override fun onStop() {
        super.onStop()
        userRef.removeEventListener(childEventListener)
    }
}
