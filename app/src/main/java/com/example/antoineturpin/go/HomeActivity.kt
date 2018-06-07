package com.example.antoineturpin.go

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.google.android.gms.tasks.Task
import android.support.annotation.NonNull
import android.view.View
import android.widget.TextView
import com.google.android.gms.tasks.OnCompleteListener
import com.firebase.ui.auth.AuthUI
import com.google.android.gms.location.places.ui.PlacePicker
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.LatLng
import android.widget.Toast
import com.google.android.gms.location.places.Place
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseError
import java.nio.file.Files.exists
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ValueEventListener


class HomeActivity : AppCompatActivity() {
    val PLACE_PICKER_REQUEST = 1


    private var placeUserValue: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val textPlace = findViewById<TextView>(R.id.textPlace)
        val textDestination = findViewById<TextView>(R.id.textDestination)
        textDestination.visibility = View.VISIBLE

        val welcomeText = findViewById<TextView>(R.id.welcomeText)
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            // Name, email address, and profile photo Url
            val name = user.displayName
            val email = user.email

            // Check if user's email is verified
            val emailVerified = user.isEmailVerified

            // The user's ID, unique to the Firebase project
            val uid = user.uid

            val welcome = "Hello $name !"
            welcomeText.setText(welcome)

            val database = FirebaseDatabase.getInstance()
            val users = database.getReference("users")
            val soloUser = database.getReference("users").child("user")
            val userCheck = soloUser.child(uid)

            val eventListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (!dataSnapshot.exists()) {
                        database.getReference("users").child("user").child(uid).setValue(name)
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            }
            userCheck.addListenerForSingleValueEvent(eventListener)


        }

        //Logout button
        val btnLogout = findViewById<Button>(R.id.buttonLogout)
        btnLogout.setOnClickListener {
            AuthUI.getInstance()
                    .signOut(this)
                    .addOnCompleteListener {
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent);
                    }
        }

        //Map button
        val btnMaping = findViewById<Button>(R.id.buttonMap)
        btnMaping.setOnClickListener {

            val builder = PlacePicker.IntentBuilder()
            val parisSud = LatLng(48.866667, 2.333333)
            val parisNord = LatLng(48.866667, 2.4044097)
            val position = LatLngBounds(
                    parisSud, parisNord)
            builder.setLatLngBounds(position)

            startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST)


        }

        //Friend button
        val btnAddFriend = findViewById<Button>(R.id.buttonFriend)
        btnAddFriend.setOnClickListener {
            val intent = Intent(this, AddFriendActivity::class.java)
            startActivity(intent);
        }

        //Go button
        val btnGo = findViewById<Button>(R.id.buttonGo)

        //Database connection & childs
        val database = FirebaseDatabase.getInstance()
        val users = database.getReference("users")
        val soloUser = database.getReference("users").child("user")
        val userCurrent = soloUser.child(user?.uid)

        val go = users.child("go")
        val placeUser = userCurrent.child("places").child("place")

        placeUser.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot is DataSnapshot) { //if no place
                    if (snapshot.value == null) {
                        placeUserValue = "Anywhere"
                    }else {
                        placeUserValue = snapshot.value as String// set variable
                    }
                    textPlace.text = placeUserValue
                    //Change text depending if user have selected a location
                    if (textPlace.text.equals("Anywhere") ) {
                        textPlace?.text = "You haven't chose a destination yet !"
                        textPlace.setTextColor(Color.parseColor("#A1A6BB"))
                        btnMaping.text = "    Choose a destination    "
                        textDestination.visibility = View.INVISIBLE
                    } else {
                        btnMaping.text = "    Change your destination    "
                        textDestination.visibility = View.VISIBLE
                        textPlace.setTextColor(Color.parseColor("#222328"))
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })



        btnGo.setOnClickListener {

            val intent = Intent(this, GoActivity::class.java)
            // Add into Go
            go.child(user?.displayName).setValue(placeUserValue ?: "Anywhere") // recuperer la variable set

            startActivity(intent);
        }


    }

    //Retrieve place selected and push it to the database
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (requestCode == PLACE_PICKER_REQUEST) {

                if (resultCode == Activity.RESULT_OK) {
                    val place = PlacePicker.getPlace(data, this)

                    val placeText = place.address

                    val user = FirebaseAuth.getInstance().currentUser
                    val database = FirebaseDatabase.getInstance()

                    val users = database.getReference("users")
                    val soloUser = database.getReference("users").child("user")
                    val userCurrent = soloUser.child(user?.uid)
                    val places = userCurrent.child("places")
                    val placeChecker = places.child("place")

                    val eventListener = object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            if (!dataSnapshot.exists()) {
                                places.child("place").setValue(placeText)
                            } else {
                                places.child("place").setValue(placeText)
                            }
                        }

                        override fun onCancelled(databaseError: DatabaseError) {}
                    }
                    places.addListenerForSingleValueEvent(eventListener)


                }
            }


    }



}