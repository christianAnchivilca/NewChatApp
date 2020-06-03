package com.christian.newchatapp.model

import android.content.Context
import com.bumptech.glide.Glide
import com.christian.newchatapp.R
import com.christian.newchatapp.util.StorageUtil
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.layout_item_person.*

class PersonItem(val person:User,val userId:String,private val context: Context):Item() {

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.txt_name_person.text = person.name
        viewHolder.txt_biografia_person.text = person.bio
        //if (person.profilePicturePath != null)
            Glide.with(context)
                .load(R.drawable.ic_account_circle_black_24dp)
                .into(viewHolder.img_icon_person)
    }

    override fun getLayout() = R.layout.layout_item_person


}