package com.christian.newchatapp.util

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.lang.NullPointerException
import java.util.*

object StorageUtil {

    private val storageInstance : FirebaseStorage by lazy {FirebaseStorage.getInstance()}

    private val currentUserRef : StorageReference
        get() = storageInstance.reference
               .child(FirebaseAuth.getInstance().currentUser?.uid ?: throw NullPointerException("UID is null"))

    fun uploadProfilePhoto(imageBytes:ByteArray,onSucces:(imagePath:String)->Unit){

        val ref = currentUserRef.child("profilePictures/${UUID.nameUUIDFromBytes(imageBytes)}")
            ref.putBytes(imageBytes)
               .addOnSuccessListener {
                 onSucces(ref.path)
            }
    }

    fun uploadProfileImages(imageBytes:ByteArray,onSucces:(imagePath:String)->Unit){

        val ref = currentUserRef.child("messages/${UUID.nameUUIDFromBytes(imageBytes)}")
        ref.putBytes(imageBytes)
            .addOnSuccessListener {
                onSucces(ref.path)
            }
    }

    fun pathToReference(path:String) = storageInstance.getReference(path)


}