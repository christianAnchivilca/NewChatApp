package com.christian.newchatapp.model

data class ChatChannel (val userIds:MutableList<String>){

    constructor():this(mutableListOf())
}