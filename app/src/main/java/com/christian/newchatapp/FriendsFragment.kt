package com.christian.newchatapp

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.christian.newchatapp.model.PersonItem
import com.christian.newchatapp.util.AppConstants
import com.christian.newchatapp.util.FirestoreUtil
import com.google.firebase.firestore.ListenerRegistration
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.OnItemClickListener
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.fragment_friends.*

class FriendsFragment:Fragment() {

    private lateinit var userListenerRegistration:ListenerRegistration
    private var shouldInitRecyclerView = true
    private lateinit var friendsSection:Section
    private var dialog:AlertDialog?=null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_friends,container,false)
        userListenerRegistration = FirestoreUtil.addUserListener(this.requireActivity(),this::updateRecyclerView)
        return view
    }



    override fun onDestroyView() {
        super.onDestroyView()
        FirestoreUtil.removeListener(userListenerRegistration)
        shouldInitRecyclerView = true
    }

    private fun updateRecyclerView(items:List<Item>){
        fun init(){
         recycler_friends.apply {
             layoutManager = LinearLayoutManager(this@FriendsFragment.context)
             adapter = GroupAdapter<ViewHolder>().apply {
                 friendsSection = Section(items)
                 add(friendsSection)
                 setOnItemClickListener(onItemClick)
             }
         }

            shouldInitRecyclerView = false

        }
        fun updateItems() = friendsSection.update(items)

        if (shouldInitRecyclerView)
             init()
        else
            updateItems()
    }

    private val onItemClick = OnItemClickListener{
        item, view ->
        if (item is PersonItem){
            val intent = Intent(context,ChatActivity::class.java)
            intent.putExtra(AppConstants.USER_NAME,item.person.name)
            intent.putExtra(AppConstants.USER_ID,item.userId)
            startActivity(intent)

        }
    }

}