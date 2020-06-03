package com.christian.newchatapp.recyclerViewItem

import android.view.Gravity
import android.widget.FrameLayout
import com.christian.newchatapp.R
import com.christian.newchatapp.model.Message
import com.google.firebase.auth.FirebaseAuth
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.item_text_message.*
import java.text.SimpleDateFormat

abstract class MessageItem(private val message:Message):Item() {

    override fun bind(viewHolder: ViewHolder, position: Int) {

        setTimeText(viewHolder)
        setMessageRootGravity(viewHolder)

    }

    private fun setTimeText(viewHolder: ViewHolder) {
        val dateFormat = SimpleDateFormat.getDateTimeInstance(
            SimpleDateFormat.SHORT,
            SimpleDateFormat.SHORT)
        viewHolder.textView_message_time.text = dateFormat.format(message.time)

    }

    private fun setMessageRootGravity(viewHolder: ViewHolder){
        if (message.senderId == FirebaseAuth.getInstance().currentUser?.uid){

            viewHolder.message_root.apply {
                setBackgroundResource(R.drawable.rect_oval_white)

                this.layoutParams = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.END)
            }

        }else{

            viewHolder.message_root.apply {
                setBackgroundResource(R.drawable.rect_round_primary_color)
                this.layoutParams = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.START)
            }
        }
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