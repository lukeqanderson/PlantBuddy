package com.example.plantbuddy

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class UpdatePlant : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_plant)

        // grabs the id for the plant name and buttons
        val waterButton = findViewById<Button>(R.id.water_button)
        val fertilizeButton = findViewById<Button>(R.id.fertilize_button)
        val harvestButton = findViewById<Button>(R.id.harvest_button)
        val backButton = findViewById<Button>(R.id.back_button)
        val plantNameLabel = findViewById<TextView>(R.id.plant_name)

        // grabs data passed for adapter
        val uid: String? = intent.getStringExtra("uid")
        val plantKey: String? = intent.getStringExtra("plantKey")
        val plantName: String? = intent.getStringExtra("plantName")
        val harvestDate: Long = intent.getLongExtra("harvestDate",0)

        // sets text to display plant name
        plantNameLabel.text = plantName

        // gets current date
        val currentDate = LocalDateTime.now()
        val simpleDateFormatter = DateTimeFormatter.BASIC_ISO_DATE
        val currentDateFormatted: Int = currentDate.format(simpleDateFormatter).toInt()

        // updates the water when clicked
        waterButton.setOnClickListener {
            if (uid != null && plantKey != null) {
                // updates db with current date
                val db: DatabaseReference =
                    FirebaseDatabase.getInstance().getReference("Users").child(uid).child("plants")
                        .child(plantKey)
                db.child("lastWateringDate").setValue(currentDateFormatted).addOnSuccessListener {
                    Toast.makeText(this@UpdatePlant,"\"$plantName\" has been watered!",Toast.LENGTH_SHORT).show()
                }

            }
        }

        // updates the fertilizer when clicked
        fertilizeButton.setOnClickListener {
            if (uid != null && plantKey != null) {
                // updates db with current date
                val db: DatabaseReference =
                    FirebaseDatabase.getInstance().getReference("Users").child(uid).child("plants")
                        .child(plantKey)
                db.child("lastFertilizingDate").setValue(currentDateFormatted).addOnSuccessListener {
                    Toast.makeText(this@UpdatePlant,"\"$plantName\" has been fertilized!",Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Only displays the harvest button if plant is harvestable
        if (harvestDate == 0.toLong()) {
            harvestButton.visibility = View.GONE
        }
        else {
            harvestButton.visibility = View.VISIBLE
        }

        // Harvests plant on click
        harvestButton.setOnClickListener {
            if (uid != null && plantKey != null) {
                // updates db with current date
                val db: DatabaseReference =
                    FirebaseDatabase.getInstance().getReference("Users").child(uid).child("plants")
                        .child(plantKey)
                db.child("harvestDate").setValue(0).addOnSuccessListener {
                    Toast.makeText(this@UpdatePlant,"\"$plantName\" has been harvested!",Toast.LENGTH_SHORT).show()
                    // hides harvest option
                    harvestButton.visibility = View.GONE
                }
            }
        }

        // Intent to go back to plant list activity
        val startPlantList = Intent(this,MainActivity::class.java)

        // takes user back to plant list when clicked
        backButton.setOnClickListener {
            startPlantList.putExtra("uid", uid)
            startActivity(startPlantList)
        }
    }
}