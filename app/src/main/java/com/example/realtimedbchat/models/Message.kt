package com.example.realtimedbchat.models

class Message {

    constructor() //empty for firebase

    constructor(messageText: String){
        text = messageText
    }
    var text: String? = null
    var timestamp: Long = System.currentTimeMillis()
}