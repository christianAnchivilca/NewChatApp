package com.christian.newchatapp

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.christian.newchatapp.service.MyFirebaseInstanceIDService
import com.christian.newchatapp.util.FirestoreUtil
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.activity_sign_in.*

class SignInActivity : AppCompatActivity() {
    private val providers = listOf(AuthUI.IdpConfig.EmailBuilder().setAllowNewAccounts(true).setRequireName(true)
        .build())
    companion object{
        private val RC_EMAIL = 1213
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        btn_signin.setOnClickListener{
            val intent = AuthUI.getInstance().createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setLogo(R.drawable.ic_fire_emoji)
                .build()
            startActivityForResult(intent,RC_EMAIL)

        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_EMAIL){
            val response = IdpResponse.fromResultIntent(data)
            if (resultCode == Activity.RESULT_OK){

                FirestoreUtil.initCurrentUserIfFirsTime {
                    startActivity(Intent(this,MainActivity::class.java))


                    FirebaseInstanceId.getInstance().instanceId
                        .addOnCompleteListener(OnCompleteListener { task ->
                            if (!task.isSuccessful) {

                                return@OnCompleteListener
                            }
                            // Get new Instance ID token
                            val registrationToken = task.result?.token
                            MyFirebaseInstanceIDService.addTokenFirestore(registrationToken!!)


                        })


                }


            }else{
                Toast.makeText(this,"Error",Toast.LENGTH_SHORT).show()
            }
        }
    }

}
