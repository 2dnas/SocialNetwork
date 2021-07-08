package com.example.socialnetwork.ui.activities

import android.app.Application
import android.os.Bundle
import com.example.socialnetwork.R
import com.example.socialnetwork.base.BaseActivity
import com.example.socialnetwork.other.goOnActivity
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject

class AuthActivity : BaseActivity() {
    @Inject lateinit var app : Application
    override fun onCreate(savedInstanceState: Bundle?) {
        injector.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)


        if(FirebaseAuth.getInstance().currentUser != null) {
            goOnActivity(MainActivity::class.java).also {
                finish()
            }
        }


    }
}