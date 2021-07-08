package com.example.socialnetwork.ui.activities

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.socialnetwork.R
import com.example.socialnetwork.base.BaseActivity
import com.example.socialnetwork.other.goOnActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.navHostFragmentMain) as NavHostFragment
        

        bottomNavigationView.apply {
            background = null
            menu.getItem(2).isEnabled = false
            setupWithNavController(navHostFragment.findNavController())
            setOnItemReselectedListener { item ->
                when(item.itemId){
                    selectedItemId -> return@setOnItemReselectedListener
                    else -> return@setOnItemReselectedListener
                }
            }
        }

        fabNewPost.setOnClickListener{

            navHostFragment.findNavController().navigate(R.id.globalActionToCreatePost)

        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.miLogOut -> {
                FirebaseAuth.getInstance().signOut()
                goOnActivity(AuthActivity::class.java).also {
                    finish()
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }
}