package com.example.realtimedbchat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.realtimedbchat.adapter.MessageAdapter
import com.example.realtimedbchat.models.Message
import com.google.firebase.FirebaseApp
import com.google.firebase.database.*


class MainActivity : AppCompatActivity() {

    var databaseReference: DatabaseReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initFirebase()

        setupSendButton()

        createFirebaseListener()
    }

    private fun initFirebase() {
        FirebaseApp.initializeApp(applicationContext)

        FirebaseDatabase.getInstance().setLogLevel(Logger.Level.DEBUG)

        databaseReference = FirebaseDatabase.getInstance().reference
    }

    private fun createFirebaseListener(){
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val toReturn: ArrayList<Message> = ArrayList()

                for(data in dataSnapshot.children){
                    val messageData = data.getValue(Message::class.java)

                    val message = messageData?.let { it } ?: continue

                    toReturn.add(message)
                }

                toReturn.sortBy { message ->
                    message.timestamp
                }

                setupAdapter(toReturn)
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        }
        databaseReference?.child("messages")?.addValueEventListener(postListener)
    }

    private fun setupAdapter(data: ArrayList<Message>){

        val mainActivityRecyclerView: RecyclerView = findViewById(R.id.mainActivityRecyclerView)

        val linearLayoutManager = LinearLayoutManager(this)
        mainActivityRecyclerView.layoutManager = linearLayoutManager
        mainActivityRecyclerView.adapter = MessageAdapter(data) {
            Toast.makeText(this, "${it.text} clicked", Toast.LENGTH_SHORT).show()
        }

        mainActivityRecyclerView.scrollToPosition(data.size - 1)
    }

    private fun setupSendButton() {

        val mainActivitySendButton:ImageView = findViewById(R.id.mainActivitySendButton)
        val mainActivityEditText:EditText = findViewById(R.id.mainActivityEditText)
        mainActivitySendButton.setOnClickListener {
            if (!mainActivityEditText.text.toString().isEmpty()){
                sendData()
            }else{
                Toast.makeText(this, "Please enter a message", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun sendData(){
        val mainActivityEditText:EditText = findViewById(R.id.mainActivityEditText)
        databaseReference?.
        child("messages")?.
        child(java.lang.String.valueOf(System.currentTimeMillis()))?.
        setValue(Message(mainActivityEditText.text.toString()))

        mainActivityEditText.setText("")
    }
}