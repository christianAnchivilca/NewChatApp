package com.christian.newchatapp

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast

import androidx.appcompat.widget.AlertDialogLayout

import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide

import com.christian.newchatapp.util.FirestoreUtil
import com.christian.newchatapp.util.StorageUtil
import com.firebase.ui.auth.AuthUI
import dmax.dialog.SpotsDialog
import java.io.ByteArrayOutputStream


class MyAccountFragment:Fragment() {

    private lateinit var selectedImageBytes:ByteArray
    private var pictureJustChanged = false
    var img_profile:ImageView?=null
    var edt_name:EditText?=null
    var edt_bio:EditText?=null
    private var dialog: AlertDialog?=null


    companion object{
        private val RC_IMAGE =12
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_my_account, container, false)

        init(view)


        return view
    }

    @SuppressLint("UseRequireInsteadOfGet")
    private fun init(view: View) {
          dialog = SpotsDialog.Builder().setContext(this@MyAccountFragment.context!!)
              .setCancelable(false).setTheme(R.style.Custom).build()

         edt_name = view.findViewById<View>(R.id.edt_name) as EditText
         edt_bio = view.findViewById<View>(R.id.edt_biografia) as EditText
        img_profile = view.findViewById<View>(R.id.imageView_profile_picture) as ImageView
        val btn_save = view.findViewById<View>(R.id.btn_save) as Button
        val btn_salir = view.findViewById<View>(R.id.btn_sign_out) as Button


        //cambiar de foto
        img_profile!!.setOnClickListener{
            val intent = Intent().apply {
                type = "image/*"
                action = Intent.ACTION_GET_CONTENT
                putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/jpeg","image/png"))
            }
            startActivityForResult(Intent.createChooser(intent,"select image"), RC_IMAGE)
        }

        //guardar , actualizar datos
        btn_save.setOnClickListener{
            if (::selectedImageBytes.isInitialized)
                StorageUtil.uploadProfilePhoto(selectedImageBytes){
                    imagePath ->
                    FirestoreUtil.updateCurrentUser(edt_name!!.text.toString(),edt_bio!!.text.toString(),imagePath)

                    Toast.makeText(this@MyAccountFragment.context,"Guardado exitosamente!",Toast.LENGTH_LONG).show()

                }
            else
                FirestoreUtil.updateCurrentUser(edt_name!!.text.toString(),edt_bio!!.text.toString(),null)
               Toast.makeText(this@MyAccountFragment.context,"Guardado exitosamente!",Toast.LENGTH_LONG).show()
        }

        //cerrar sesion de la aplicacion
        btn_salir.setOnClickListener{
            AuthUI.getInstance().signOut(this@MyAccountFragment.context!!)
                .addOnCompleteListener{
                    task ->
                    if (task.isSuccessful){
                        startActivity(Intent(this@MyAccountFragment.context!!,SignInActivity::class.java))
                    }
                }
        }



    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_IMAGE && resultCode == Activity.RESULT_OK && data != null && data.data !=null){
            val selectedImagePath = data.data
            val selectedImageBmp = MediaStore.Images.Media
                .getBitmap(activity?.contentResolver,selectedImagePath)

            val outputStream = ByteArrayOutputStream()
            selectedImageBmp.compress(Bitmap.CompressFormat.JPEG,90,outputStream)
            selectedImageBytes = outputStream.toByteArray()

            Glide.with(this)
                .load(selectedImageBytes)
                //.load(R.drawable.ic_account_circle_black_24dp)
                .into(img_profile!!)

            pictureJustChanged = true

        }
    }

    override fun onStart() {
        super.onStart()
        dialog!!.show()
        FirestoreUtil.getCurrentUser {
            user->
            if (this@MyAccountFragment.isVisible){
                edt_name!!.setText(user!!.name)
                edt_bio!!.setText(user!!.bio)
               if (!pictureJustChanged && user.profilePicturePath != null)
                   Glide.with(this)
                       .load(R.drawable.ic_account_circle_black_24dp)
                       //.load(StorageUtil.pathToReference(user.profilePicturePath))
                       .into(img_profile!!)
                dialog!!.dismiss()
            }
        }
    }
}