package com.example.antoineturpin.go

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.SearchView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class AddFriendActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_friend)


        val user = FirebaseAuth.getInstance().currentUser
        val uid = user?.uid
        val database = FirebaseDatabase.getInstance()
        val users = database.getReference("users")
        val soloUser = database.getReference("users").child("user")


        //Search View
        val btnSearch = findViewById<SearchView>(R.id.buttonSearchFriend)
        btnSearch.setIconifiedByDefault(false)
        btnSearch.setOnClickListener {

        }
    }
}
