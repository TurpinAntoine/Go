package com.example.antoineturpin.go

import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.antoineturpin.go.item.ReadyItem
import com.example.antoineturpin.go.model.Ready
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter
import kotlinx.android.synthetic.main.activity_go.*
import kotlinx.android.synthetic.main.activity_recap.*

class RecapActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recap)

        //Connection to database & childs
        val database = FirebaseDatabase.getInstance()
        val user = FirebaseAuth.getInstance().currentUser
        val users = database.getReference("users")
        val soloUser = database.getReference("users").child("user")
        val userCurrent = soloUser.child(user?.uid)
        val go = users.child("go")

        //get intent from Go
        val intent = intent
        val placeSelected = intent.getStringExtra("place")

        //Destination title
        val destinationTitle = findViewById<TextView>(R.id.textDestination)
        destinationTitle.text = placeSelected

        //Ok button
        val btnOk = findViewById<Button>(R.id.buttonOk)

        //Back to go & send back the place previously selected to avoid update
        btnOk.setOnClickListener{
            val intentGo = Intent(this, GoActivity::class.java)
            intentGo.putExtra("place", placeSelected)
            startActivity(intentGo);
        }

        //Go to destination
        val btnGo = findViewById<Button>(R.id.buttonGoMap)
        var place = ""

        //Open maps at destination
        btnGo.setOnClickListener{

            val selectionListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (place != dataSnapshot.value as String) {

                        place = dataSnapshot.value as String
                        val selectionPlace = Uri.parse("geo:48.866667,2.333333?z=10&q=" + Uri.encode(place))

                        val mapIntent = Intent(Intent.ACTION_VIEW, selectionPlace)
                        mapIntent.`package` = "com.google.android.apps.maps"
                        if (mapIntent.resolveActivity(packageManager) != null) {
                            startActivity(mapIntent);
                        }

                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Toast
                            .makeText(this@RecapActivity, "Unexpected Error", Toast.LENGTH_SHORT)
                            .show()
                }



            }
            database.getReference("users").child("go-selection").addValueEventListener(selectionListener)

        }

        // RecyclerView
        val friends = arrayListOf<Ready>()

        listReady.layoutManager = LinearLayoutManager(this)

        //Fill user group
        val goListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                friends.clear()

                val listened = dataSnapshot.value


                for (snapshot in dataSnapshot.children) {
                    val name = snapshot.key
                    friends.add(Ready(name))

                }

                //Vertical configuration

                listReady.layoutManager = LinearLayoutManager(this@RecapActivity,
                        LinearLayoutManager.VERTICAL, false);

                val itemAdapter = FastItemAdapter<ReadyItem>()

                // link adapter to view (RecyclerView)
                listReady.adapter = itemAdapter

                for(contact in friends) {
                    val contactItem = ReadyItem(contact)
                    itemAdapter.add(contactItem)
                }

            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast
                        .makeText(this@RecapActivity, "Try again later", Toast.LENGTH_SHORT)
                        .show()
            }



        }
        database.getReference("users").child("go").addValueEventListener(goListener)


    }


}
