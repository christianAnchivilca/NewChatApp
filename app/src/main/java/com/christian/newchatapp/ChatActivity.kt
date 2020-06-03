package com.christian.newchatapp

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Message
import android.provider.MediaStore
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.christian.newchatapp.model.ImageMessage
import com.christian.newchatapp.model.MessageType
import com.christian.newchatapp.model.TextMessage
import com.christian.newchatapp.model.User
import com.christian.newchatapp.util.AppConstants
import com.christian.newchatapp.util.FirestoreUtil
import com.christian.newchatapp.util.StorageUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ListenerRegistration
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.ViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.activity_chat.*
import java.io.ByteArrayOutputStream
import java.util.*

class ChatActivity : AppCompatActivity() {
    private lateinit var messagesListenerRegistration:ListenerRegistration
    private var shouldInitRecyclerView = true
    private lateinit var messagesSection:Section
    private lateinit var currentChannelId:String
    private lateinit var currentUser:User
    private lateinit var otherUserId:String

    companion object{
        private val RC_SEND_IMAGE = 1212
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = intent.getStringExtra(AppConstants.USER_NAME)

        FirestoreUtil.getCurrentUser {
            currentUser = it!!
        }

        otherUserId = intent.getStringExtra(AppConstants.USER_ID)


        FirestoreUtil.getOrCreateChatChannel(otherUserId){
                channelId ->

            currentChannelId = channelId
            messagesListenerRegistration = FirestoreUtil.addChatMessagesListener(channelId,this,this::updateRecyclerView)


             img_send.setOnClickListener{

                 val textMessages = TextMessage(edt_message.text.toString(),Calendar.getInstance().time,
                     FirebaseAuth.getInstance().currentUser!!.uid,otherUserId,currentUser.name)
                 edt_message.setText("")
                 FirestoreUtil.sendMessages(textMessages,channelId)
             }

            fab_send_image.setOnClickListener{
                val intent = Intent().apply {
                    type = "image/*"
                    action = Intent.ACTION_GET_CONTENT
                    putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/jpeg","image/png"))
                }
                startActivityForResult(Intent.createChooser(intent,"seleccione image"),RC_SEND_IMAGE)
            }

        }
    }

    private fun updateRecyclerView(messages:List<Item>){
        fun init(){
            recycler_chat_messages.apply {
                layoutManager = LinearLayoutManager(this@ChatActivity)
                adapter = GroupAdapter<ViewHolder>().apply {
                    messagesSection = Section(messages)
                    this.add(messagesSection)
                }
            }
            shouldInitRecyclerView = false

        }
        fun updateItems() = messagesSection.update(messages)
        if(shouldInitRecyclerView)
            init()
        else
            updateItems()
        recycler_chat_messages.scrollToPosition(recycler_chat_messages.adapter!!.itemCount -1)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SEND_IMAGE && resultCode == Activity.RESULT_OK && data != null && data.data != null){

            val selectedImagePath = data.data
            val selectedImageBitmap = MediaStore.Images.Media.getBitmap(contentResolver,selectedImagePath)
            val outputStream = ByteArrayOutputStream()
            selectedImageBitmap.compress(Bitmap.CompressFormat.JPEG,90,outputStream)
            val selectedImageBytes = outputStream.toByteArray()

            StorageUtil.uploadProfileImages(selectedImageBytes){
                imagePath ->
                val messageToSend = ImageMessage(imagePath,Calendar.getInstance().time,
                    FirebaseAuth.getInstance().currentUser!!.uid,otherUserId,currentUser.name)
                FirestoreUtil.sendMessages(messageToSend,currentChannelId)
            }

        }
    }


}
