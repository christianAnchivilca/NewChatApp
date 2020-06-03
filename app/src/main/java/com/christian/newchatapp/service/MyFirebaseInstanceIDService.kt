package com.christian.newchatapp.service

import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessagingService
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.util.Log
import com.christian.newchatapp.util.FirestoreUtil
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import java.lang.NullPointerException


class MyFirebaseInstanceIDService: FirebaseMessagingService() {


    override fun onNewToken(newToken: String?) {
        super.onNewToken(newToken)
        if (FirebaseAuth.getInstance().currentUser != null)
            addTokenFirestore(newToken!!)

    }

    companion object{
        fun addTokenFirestore(newRegistrationToken:String){
            if (newRegistrationToken == null)throw NullPointerException("FCM TOKEN IS NULL")
            FirestoreUtil.getFCMRegistrationTokens { tokens ->
                if (tokens.contains(newRegistrationToken)){
                    return@getFCMRegistrationTokens
                }

                tokens.add(newRegistrationToken)
                FirestoreUtil.setFCMRegistrationTokens(tokens)
            }

        }
    }
}