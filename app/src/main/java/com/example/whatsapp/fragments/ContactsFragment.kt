package com.example.whatsapp.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.whatsapp.activities.GroupActivity

import com.example.whatsapp.R
import com.example.whatsapp.activities.ChatActivity
import com.example.whatsapp.adapter.ContactsAdapter
import com.example.whatsapp.config.FirebaseConfig
import com.example.whatsapp.constants.Constants
import com.example.whatsapp.helper.RecyclerItemClickListener
import com.example.whatsapp.helper.UserFirebase
import com.example.whatsapp.model.ChatModel
import com.example.whatsapp.model.UserModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_contacts.*


class ContactsFragment : Fragment() {

    private val contactList: MutableList<UserModel> = arrayListOf()
    private var userRef = FirebaseConfig.getFirebaseDb().child(Constants.DB.USERS)

    private lateinit var valueEventListener: ValueEventListener
    private lateinit var mRecyclerContacts: RecyclerView
    private var adapter: ContactsAdapter = ContactsAdapter(mutableListOf(), null)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_contacts, container, false)

        mRecyclerContacts = view.findViewById(R.id.recyclerContacts)
        loadRecycler()
        setListeners()

        return view
    }

    private fun loadItemGroup() {

        // empty email is going to be used as header
        val itemGroup = UserModel()
        itemGroup.name = "New Group"
        itemGroup.email = ""

        contactList.add(itemGroup)
    }

    private fun setListeners() {
        mRecyclerContacts.addOnItemTouchListener(
            RecyclerItemClickListener(
                activity,
                mRecyclerContacts,
                object : RecyclerItemClickListener.OnItemClickListener {
                    override fun onItemClick(view: View?, position: Int) {

                        val userListUpdated = adapter.getContacts()

                        val userSelected = userListUpdated[position]
                        val header = userSelected.email.isNullOrEmpty()

                        if(header) {
                            val intent = Intent(activity, GroupActivity::class.java)
                            startActivity(intent)
                        } else {
                            val intent = Intent(activity, ChatActivity::class.java)
                            intent.putExtra("chatContact", userSelected)
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

    private fun loadRecycler() {

        //configure adapter
        adapter = ContactsAdapter(contactList, this.activity)
        mRecyclerContacts.adapter = adapter
        //configure recycle view
        mRecyclerContacts.layoutManager = LinearLayoutManager(this.activity)
        mRecyclerContacts.setHasFixedSize(true)

    }

    private fun recoverUsers() {
        valueEventListener = userRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(databaseError: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val currentUser = UserFirebase.getLoggedUserData()

                contactList.clear()

                loadItemGroup()

                for (data in dataSnapshot.children) {
                    val user = data.getValue(UserModel::class.java)

                    if (user?.email != currentUser.email) {
                        contactList.add(user!!)
                    }
                }
                recyclerContacts.adapter?.notifyDataSetChanged()
            }
        })
    }

    fun searchContacts(text: String) {

        val list: MutableList<UserModel> = arrayListOf()

        for (user in contactList) {
            val name = user.name?.toLowerCase()
            if (name!!.contains(text)) {
                list.add(user)
            }
        }

            adapter = ContactsAdapter(list, activity)
            mRecyclerContacts.adapter = adapter
            mRecyclerContacts.adapter?.notifyDataSetChanged()
        }


    fun loadContacts() {
        mRecyclerContacts.adapter = ContactsAdapter(contactList, activity)
        mRecyclerContacts.adapter?.notifyDataSetChanged()
    }

    override fun onStart() {
        super.onStart()
        recoverUsers()
    }

    override fun onStop() {
        super.onStop()
        userRef.removeEventListener(valueEventListener)
    }

}
