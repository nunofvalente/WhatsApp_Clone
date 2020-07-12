package com.example.whatsapp.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.example.whatsapp.R
import com.example.whatsapp.config.FirebaseConfig
import com.example.whatsapp.fragments.ChatsFragment
import com.example.whatsapp.fragments.ContactsFragment
import com.miguelcatalan.materialsearchview.MaterialSearchView
import com.miguelcatalan.materialsearchview.MaterialSearchView.SearchViewListener
import com.ogaclejapan.smarttablayout.SmartTabLayout
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.toolbar.toolbar


class MainActivity : AppCompatActivity() {

    private val auth = FirebaseConfig.getFirebaseAuth()

    private lateinit var searchView: MaterialSearchView
    private lateinit var adapter: FragmentPagerItemAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        searchView = findViewById<View>(R.id.search_view) as MaterialSearchView


        setListeners()
        bindViewPager()
    }

    private fun bindViewPager() {
        adapter = FragmentPagerItemAdapter(
            supportFragmentManager, FragmentPagerItems.with(this)
                .add(R.string.chats, ChatsFragment::class.java)
                .add(R.string.contacts, ContactsFragment::class.java)
                .create()
        )

        val viewPager = findViewById<ViewPager>(R.id.viewPager)
        viewPager.adapter = adapter

        val viewPagerTab = findViewById<View>(R.id.viewPagerTab) as SmartTabLayout
        viewPagerTab.setViewPager(viewPager)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_list, menu)

        val item = menu!!.findItem(R.id.action_search)
        searchView.setMenuItem(item)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_Settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                finish()
                return true
            }
            R.id.menu_signOut -> {
                signOut()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setListeners() {

        searchView.setOnQueryTextListener(object : MaterialSearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                //Do some magic
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                if (viewPager.currentItem == 0) {
                    val chatsFragment = adapter.getPage(0) as ChatsFragment
                    if (newText != "" && newText.isNotEmpty()) {
                        chatsFragment.searchChat(newText)
                    } else {
                        chatsFragment.loadChat()
                    }
                } else if (viewPager.currentItem == 1) {
                        val contactsFragment = adapter.getPage(1) as ContactsFragment
                        if (newText != "" && newText.isNotEmpty()) {
                            contactsFragment.searchContacts(newText)
                        } else {
                            contactsFragment.loadContacts()
                        }
                    }
                return true
            }
        })

        searchView.setOnSearchViewListener(object : SearchViewListener {
            override fun onSearchViewShown() {
                //Do some magic
            }

            override fun onSearchViewClosed() {
                val fragment = adapter.getPage(0) as ChatsFragment

                fragment.loadChat()
            }
        })
    }

    private fun signOut() {
        auth.signOut()
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}
