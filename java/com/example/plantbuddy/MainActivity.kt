package com.example.plantbuddy

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class MainActivity : AppCompatActivity() {

    // var for firebase realtime database
    private lateinit var db: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // grabs newPlantButton by id
        val newPlantButton = findViewById<Button>(R.id.new_plant_button)

        // grabs sign out id
        val signOutText = findViewById<TextView>(R.id.sign_out)

        // gets intent with uid from sign in
        val uid: String? = intent.getStringExtra("uid")

        // gets reference to users in the database
        db = FirebaseDatabase.getInstance().getReference("Users")

        // declares variable for new recycler view
        val plantRecyclerView: RecyclerView = findViewById(R.id.plantListComponent)
        plantRecyclerView.layoutManager = LinearLayoutManager(this)
        plantRecyclerView.setHasFixedSize(true)

        // creates mutable list to store data
        val plantList = MutableList(0) { Plant() }

        // sets the recycle view to plant list
        plantRecyclerView.adapter = uid?.let { PlantListAdapter(plantList, this@MainActivity, it) }

        // retrieves plant data from database if we have a valid UID
        if (!uid.isNullOrEmpty()) {
            db.child(uid).child("plants").addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    // clears list to prevent duplicate data
                    plantList.clear()
                    for (plant in snapshot.children) {
                        // prevents out of index error on deletion change
                        if (plant == null) break
                        // gets values for fields for each plant id
                        db.child(uid).child("plants").child(plant.key.toString())
                            .addValueEventListener(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    // creates a new plant object with all the values
                                    val plantName = snapshot.child("plantName").value
                                    val daysPerWatering = snapshot.child("daysPerWatering").value
                                    val daysPerFertilizing =
                                        snapshot.child("daysPerFertilizing").value
                                    val lastWateringDate = snapshot.child("lastWateringDate").value
                                    val lastFertilizingDate =
                                        snapshot.child("lastFertilizingDate").value
                                    val harvestDate = snapshot.child("harvestDate").value
                                    val plantKey = snapshot.child("plantKey").value

                                    if (plantName != null) {

                                    val newPlant = Plant(
                                        plantName as String,
                                        daysPerWatering as Long,
                                        daysPerFertilizing as Long,
                                        lastWateringDate as Long,
                                        lastFertilizingDate as Long,
                                        harvestDate as Long
                                    )
                                    newPlant.plantKey = plantKey as String
                                    newPlant.updateNextWateringDate()
                                    newPlant.updateNextFertilizingDate()

                                    // adds new plant to the mutable list
                                    plantList.add(newPlant)

                                    // sets the recycle view to plant list
                                    plantRecyclerView.adapter =
                                        PlantListAdapter(plantList, this@MainActivity, uid)
                                    }
                                }

                                override fun onCancelled(error: DatabaseError) {
                                }
                            })
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                }
            })
        }

        // sets to open new plant details activity on click
        newPlantButton.setOnClickListener {
            val startPlantDetails = Intent(this, NewPlantDetails::class.java)
            startPlantDetails.putExtra("uid", uid)
            startActivity(startPlantDetails)
        }

        // adds on click for user sign out
        signOutText.setOnClickListener {
            // signs out current user
            val startSignIn = Intent(this, SignIn::class.java)
            startActivity(startSignIn)
        }
    }
}