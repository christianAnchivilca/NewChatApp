package com.christian.newchatapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth

class SplashActitivy : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if(FirebaseAuth.getInstance().currentUser == null)
            startActivity(Intent(this,SignInActivity::class.java))
        else
            startActivity(Intent(this,MainActivity::class.java))
        finish()



    }
}
