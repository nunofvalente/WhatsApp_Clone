package com.example.whatsapp.activities

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.whatsapp.R
import com.example.whatsapp.adapter.ContactsAdapter
import com.example.whatsapp.adapter.GroupAdapter
import com.example.whatsapp.config.FirebaseConfig
import com.example.whatsapp.constants.Constants
import com.example.whatsapp.helper.RecyclerItemClickListener
import com.example.whatsapp.helper.UserFirebase
import com.example.whatsapp.model.UserModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

import kotlinx.android.synthetic.main.activity_group_activity.*
import java.io.Serializable

class GroupActivity : AppCompatActivity() {

    private lateinit var mRecyclerViewMembers: RecyclerView
    private lateinit var mRecyclerViewMembersSelected: RecyclerView
    private lateinit var valueEventListenerMembers: ValueEventListener


    private var userRef = FirebaseConfig.getFirebaseDb().child(Constants.DB.USERS)
    private var membersList: MutableList<UserModel> = arrayListOf()
    private var membersSelectedList = arrayListOf<UserModel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_activity)
        setSupportActionBar(toolbar)

        supportActionBar?.title = "New Group"

        mRecyclerViewMembers = findViewById(R.id.recyclerMembers)
        mRecyclerViewMembersSelected = findViewById(R.id.recyclerMembersSelected)


        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        loadRecyclerMembers()
        loadRecyclerMembersSelected()
        setListeners()
    }

    private fun updateToolbarMembers() {
        val totalSelected = membersSelectedList.size
        val totalMembers = membersList.size + totalSelected

        toolbar.subtitle = "$totalSelected of $totalMembers contacts selected"
    }

    private fun setListeners() {
        mRecyclerViewMembers.addOnItemTouchListener(RecyclerItemClickListener(this, mRecyclerViewMembers, object: RecyclerItemClickListener.OnItemClickListener{
            override fun onItemClick(view: View?, position: Int) {
                val userSelected = membersList[position]

                membersSelectedList.add(userSelected)
                membersList.remove(userSelected)

                mRecyclerViewMembers.adapter?.notifyDataSetChanged()
                mRecyclerViewMembersSelected.adapter?.notifyDataSetChanged()

                updateToolbarMembers()
            }

            override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                TODO("Not yet implemented")
            }

            override fun onLongItemClick(view: View?, position: Int) {
                TODO("Not yet implemented")
            }
        }))

        mRecyclerViewMembersSelected.addOnItemTouchListener(RecyclerItemClickListener(this, mRecyclerViewMembersSelected, object: RecyclerItemClickListener.OnItemClickListener{
            override fun onItemClick(view: View?, position: Int) {
                val userSelected = membersSelectedList[position]

                membersList.add(userSelected)
                membersSelectedList.remove(userSelected)

                mRecyclerViewMembers.adapter?.notifyDataSetChanged()
                mRecyclerViewMembersSelected.adapter?.notifyDataSetChanged()

                updateToolbarMembers()
            }

            override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                TODO("Not yet implemented")
            }

            override fun onLongItemClick(view: View?, position: Int) {
                TODO("Not yet implemented")
            }
        }))


        fabForwardRegister.setOnClickListener {
            val intent = Intent(this, RegisterGroupActivity::class.java)
            intent.putExtra("members",  membersSelectedList as Serializable)

            startActivity(intent)}
    }

    private fun loadRecyclerMembersSelected() {
        mRecyclerViewMembersSelected.adapter = GroupAdapter(membersSelectedList, this)

        mRecyclerViewMembersSelected.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        mRecyclerViewMembersSelected.setHasFixedSize(true)
    }

    private fun loadRecyclerMembers() {
        mRecyclerViewMembers.adapter = ContactsAdapter(membersList ,this)

        mRecyclerViewMembers.layoutManager = LinearLayoutManager(this)
        mRecyclerViewMembers.setHasFixedSize(true)

    }


    private fun recoverUsers() {
        valueEventListenerMembers = userRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(databaseError: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val currentUser = UserFirebase.getLoggedUserData()

                membersList.clear()

                for (data in dataSnapshot.children) {
                    val user = data.getValue(UserModel::class.java)

                    if (user?.email != currentUser.email) {
                        membersList.add(user!!)
                    }
                }
                mRecyclerViewMembers.adapter?.notifyDataSetChanged()
                updateToolbarMembers()
            }
        })
    }

    override fun onStart() {
        super.onStart()
        recoverUsers()
    }

    override fun onStop() {
        super.onStop()
        userRef.removeEventListener(valueEventListenerMembers)
    }

}
