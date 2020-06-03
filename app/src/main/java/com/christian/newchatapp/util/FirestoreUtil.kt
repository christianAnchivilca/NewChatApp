package com.christian.newchatapp.util

import android.content.Context
import android.util.Log
import com.christian.newchatapp.model.*
import com.christian.newchatapp.recyclerViewItem.ImageMessageItem
import com.christian.newchatapp.recyclerViewItem.TextMessageItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.storage.FirebaseStorage
import com.xwray.groupie.kotlinandroidextensions.Item
import java.lang.NullPointerException

object FirestoreUtil {

    private val firestoreInstance:FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    private val currentUserDocRef:DocumentReference
          get() = firestoreInstance.document("users/${FirebaseAuth.getInstance().currentUser?.uid?:
                     throw NullPointerException("UID is null")}")

    private val chatChannelsCollections = firestoreInstance.collection("chatChannels")

    fun initCurrentUserIfFirsTime(onComplete:()->Unit){

        currentUserDocRef.get()
            .addOnSuccessListener {
            documentSnapshot ->
            if (!documentSnapshot.exists()){ // SI NO EXISTE EL USUARIO
                val newUser = User(FirebaseAuth.getInstance().currentUser?.displayName ?: "","",null,
                    mutableListOf())
                currentUserDocRef.set(newUser).addOnSuccessListener {
                    onComplete()
                }
            }else{
                onComplete()
            }
        }

    }

    fun updateCurrentUser(name:String="",bio:String="",profilePicturePath:String?=null){
        val userFieldMap = mutableMapOf<String,Any>()



        if (name.isNotBlank())userFieldMap["name"] = name
        if (bio.isNotBlank())userFieldMap["bio"] = bio
        if (profilePicturePath != null)
            userFieldMap["profilePicturePath"] = profilePicturePath

        currentUserDocRef.update(userFieldMap)

    }

    fun getCurrentUser(onComplete: (User?) -> Unit){
        currentUserDocRef.get()
            .addOnSuccessListener {
                onComplete(it.toObject(User::class.java))
            }

    }

    fun addUserListener(context: Context,onListen:(List<Item>) -> Unit) : ListenerRegistration {

        // detectar un documento con el método onSnapshot().
        return firestoreInstance.collection("users")
            .addSnapshotListener{ querySnapshot, firebaseFirestoreException ->

                if (firebaseFirestoreException != null){
                    Log.e("FIRESTORE","Users listener error",firebaseFirestoreException)
                    return@addSnapshotListener
                }

                val items = mutableListOf<Item>()
                querySnapshot!!.documents.forEach {
                    if (it.id != FirebaseAuth.getInstance().currentUser?.uid)
                        items.add(PersonItem(it.toObject(User::class.java)!!, it.id,context!!))
                }

                onListen(items)
            }

    }

    fun removeListener(registration:ListenerRegistration) = registration.remove()

    fun getOrCreateChatChannel(otherUserId:String,onComplete: (channelId:String) -> Unit){
        currentUserDocRef.collection("engagedChatChannels")
            .document(otherUserId).get().addOnSuccessListener {
                if (it.exists()){
                    onComplete(it["channelId"] as String)
                    return@addOnSuccessListener
                }

                //SE CREA LA SALA DE CHAT
                val currentUserId = FirebaseAuth.getInstance().currentUser!!.uid
                val newChannel = chatChannelsCollections.document()
                newChannel.set(ChatChannel(mutableListOf(currentUserId,otherUserId)))

                // SE CREAR UNA COLECCION PARA AMBAS PERSONAS QUE SE UNIERON EN UNA SALA DE CHAT PREVIAMENTE
                currentUserDocRef.collection("engagedChatChannels") // USUARIO ACTUAL
                    .document(otherUserId)
                    .set(mapOf("channelId" to newChannel.id))

                firestoreInstance.collection("users").document(otherUserId) // LA OTRA PERSONA
                    .collection("engagedChatChannels")
                    .document(currentUserId)
                    .set(mapOf("channelId" to newChannel.id))

                onComplete(newChannel.id)

            }
    }

    fun addChatMessagesListener(channelId: String,context: Context,onListen: (List<Item>) -> Unit):ListenerRegistration{

        //se crea un coleccion para los mensajes que se realiza en la sala de chat
        return chatChannelsCollections.document(channelId).collection("messages")
            .orderBy("time")
            .addSnapshotListener{
                querySnapshot, firebaseFirestoreException ->
                if (firebaseFirestoreException != null){ // SI ES QUE HUBIERA UN ERROR
                    Log.d("FIRESTORE","ChatMessageesListener error",firebaseFirestoreException)
                    return@addSnapshotListener
                }

                val items = mutableListOf<Item>()  // OBTENEMOS LOS MENSAJES
                querySnapshot!!.documents.forEach{
                    if (it["type"]== MessageType.TEXT)
                           items.add(TextMessageItem(it.toObject(TextMessage::class.java)!!,context!!))
                    else
                        items.add(ImageMessageItem(it.toObject(ImageMessage::class.java)!!,context!!))
                }

                onListen(items)
            }

    }

    fun sendMessages(message:Message,channelId:String){
        chatChannelsCollections.document(channelId)
            .collection("messages")
         //Agregando documentos-> Pero a veces no hay un ID significativo para el documento y es más conveniente dejar que Cloud Firestore genere automáticamente un ID. Para hacerlo, llama a add():
            .add(message) //

    }

    //registrar token
    fun getFCMRegistrationTokens(onComplete: (tokens:MutableList<String>) -> Unit){
        currentUserDocRef.get().addOnSuccessListener {
            val user = it.toObject(User::class.java)!!
            onComplete(user.registrationTokens)
        }

    }

    fun setFCMRegistrationTokens(registrationTokens:MutableList<String>){
        currentUserDocRef.update(mapOf("registrationTokens" to registrationTokens))

    }



}