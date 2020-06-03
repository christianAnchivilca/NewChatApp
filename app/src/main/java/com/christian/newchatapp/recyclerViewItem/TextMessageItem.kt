package com.christian.newchatapp.recyclerViewItem

import android.content.Context
import android.view.Gravity
import android.widget.FrameLayout

import com.christian.newchatapp.model.TextMessage
import com.google.firebase.auth.FirebaseAuth
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.item_text_message.*
import java.text.SimpleDateFormat

import com.christian.newchatapp.R
import com.christian.newchatapp.model.Message


class TextMessageItem(val message:TextMessage,val context:Context):MessageItem(message) {


    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.textView_message_text.text = message.text
        super.bind(viewHolder, position)
    }

    override fun getLayout() = R.layout.item_text_message

    override fun isSameAs(other: com.xwray.groupie.Item<*>?): Boolean {
        if (other !is TextMessageItem)
            return false
        if (this.message != other.message)
            return false
        return true
    }

    override fun equals(other: Any?): Boolean {
        return isSameAs(other as? TextMessageItem)
    }


}