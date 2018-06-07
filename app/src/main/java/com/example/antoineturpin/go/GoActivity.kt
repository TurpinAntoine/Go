package com.example.antoineturpin.go

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.example.antoineturpin.go.model.Friend
import com.example.antoineturpin.go.item.FriendItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.mikepenz.fastadapter.IAdapter
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter
import com.mikepenz.fastadapter.listeners.OnClickListener
import kotlinx.android.synthetic.main.activity_go.*
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ValueEventListener
import java.lang.reflect.Array
import com.google.firebase.database.GenericTypeIndicator
import java.util.*


class GoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_go)

        //get intent from Recap
        val intent = intent
        var placeUserValue = intent.getStringExtra("place")


        //get database & childs
        val user = FirebaseAuth.getInstance().currentUser
        val database = FirebaseDatabase.getInstance()
        val users = database.getReference("users")
        val soloUser = database.getReference("users").child("user")
        val userCurrent = soloUser.child(user?.uid)
        val go = users.child("go")


        //Cancel button
        val btnUngo = findViewById<Button>(R.id.buttonUngo)

        //Onclick unset from go in database
        btnUngo.setOnClickListener {
            val intentUngo = Intent(this, HomeActivity::class.java)

            startActivity(intentUngo);

            go.child(user?.displayName).setValue(null)
        }

        // RecyclerView
        val friends = arrayListOf<Friend>()

        listGo.layoutManager = LinearLayoutManager(this)

        //Match with friend and setup layout manager
        val goListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                friends.clear()

                val listened = dataSnapshot.value


                for (snapshot in dataSnapshot.children) {
                    val name = snapshot.key

                    val place = snapshot.value as String

                    val query = userCurrent.child("friends").equalTo(name)

                    if (query != {}) {
                        friends.add(Friend(name, place))
                    }
                }

                // Vertical configuration

                listGo.layoutManager = LinearLayoutManager(this@GoActivity,
                        LinearLayoutManager.VERTICAL, false);

                val itemAdapter = FastItemAdapter<FriendItem>()

                // link adapter to view (RecyclerView)
                listGo.adapter = itemAdapter

                for(contact in friends) {
                    val contactItem = FriendItem(contact)
                    itemAdapter.add(contactItem)
                }

            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast
                        .makeText(this@GoActivity, "Try again later", Toast.LENGTH_SHORT)
                        .show()
            }



        }
        database.getReference("users").child("go").addValueEventListener(goListener)

        val placeSelected= arrayListOf<Friend>()
        var userGo = false
        var intentSelection = ""

        //Random a place for the friends between what they picks except "Anywhere"
        val mapListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val listened = dataSnapshot.value

                for (snapshot in dataSnapshot.children) {
                    val name = snapshot.key
                    val place = snapshot.value as String
                    if (place != "Anywhere")
                        placeSelected.add(Friend(name, place))
                    if (name == user?.displayName)
                        userGo = true
                }

                val count = placeSelected.size


                if (userGo == true && placeUserValue == null) {
                    val r = Random()
                    val min = 0
                    val randInt = r.nextInt(count - min) + min

                    val selection = placeSelected[randInt].Place
                    intentSelection = selection
                    placeUserValue = selection

                    users.child("go-selection").setValue(selection)
                } else {
                    users.child("go-selection").setValue(placeUserValue)
                    intentSelection = placeUserValue
                }


            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast
                        .makeText(this@GoActivity, "Unexpected Error", Toast.LENGTH_SHORT)
                        .show()
            }

        }
        database.getReference("users").child("go").addValueEventListener(mapListener)

        //Button Ready
        val btnGo = findViewById<Button>(R.id.buttonGoMap)
        //Send the random place selected to the next view
        btnGo.setOnClickListener{
            intentSelection = placeUserValue
            val intentRecap = Intent(this, RecapActivity::class.java)
            intentRecap.putExtra("place", intentSelection)
            startActivity(intentRecap);
        }

    }

}
