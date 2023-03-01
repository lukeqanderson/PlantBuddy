package com.example.plantbuddy

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.plantbuddy.R.id.cancel_plant
import com.google.firebase.database.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class NewPlantDetails : AppCompatActivity() {

    // var for firebase realtime database
    private lateinit var db : DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_plant_details)

        // grabs cancelButton by id
        val cancelButton = findViewById<Button>(cancel_plant)

        // Intent to go back to plant list activity
        val startPlantList = Intent(this,MainActivity::class.java)

        // gets unique key for user
        val uid : String? = intent.getStringExtra("uid")

        // sets to go back to plant list activity on click
        cancelButton.setOnClickListener {
            startPlantList.putExtra("uid", uid)
            startActivity(startPlantList)
        }

        // gets reference to plants in database
        db = FirebaseDatabase.getInstance().getReference("Users")

        // grabs all variables for form data
        val plantName = findViewById<EditText>(R.id.plant_name)
        val daysPerWatering = findViewById<EditText>(R.id.days_per_watering)
        val lastWateringDate = findViewById<EditText>(R.id.last_watering_date)
        val daysPerFertilizing = findViewById<EditText>(R.id.days_per_fertilizing)
        val lastFertilizingDate = findViewById<EditText>(R.id.last_fertilizing_date)
        val harvestDate = findViewById<EditText>(R.id.harvest_date)


        // grabs add button by id
        val addButton = findViewById<Button>(R.id.add_plant)

        // adds plant on click if details are correct
        addButton.setOnClickListener {

            // variable to ensure whole form is valid
            var isValidForm = true

            // FORM VALIDATION

            // validates and sets plant name
            val plantNameText : String = plantName.text.toString()
            if (plantNameText.isEmpty()) {
                // don't accept form if the name is empty
                isValidForm = false
                Toast.makeText(this@NewPlantDetails, "\"Plant Name\" is required.", Toast.LENGTH_SHORT).show()
            }

            // validates and sets days per watering
            var daysPerWateringLong = 0.toLong()
            try {
                daysPerWateringLong = daysPerWatering.text.toString().toLong()
                if (daysPerWateringLong < 1) {
                    // don't accept form if the days per watering is empty or less than 0
                    isValidForm = false
                    Toast.makeText(this@NewPlantDetails, "\"Days Per Watering\" must be 1 or greater.", Toast.LENGTH_SHORT).show()
                }
            }
            catch(e:java.lang.NumberFormatException) {
                isValidForm = false
                Toast.makeText(this@NewPlantDetails, "\"Days Per Watering\" must be a valid number.", Toast.LENGTH_SHORT).show()
            }

            // function to check for valid dates
            fun isValidDate(date: Long): Boolean {

                // makes date mutable
                var tempDate = date

                // strips away the year, day, and month
                val day: Long = tempDate % 100
                tempDate /= 100
                val month: Long = tempDate % 100
                tempDate /= 100
                val year: Long = tempDate
                var isLeapYear = false

                // checks for if it's a leap year
                if ((year % 4 == 0.toLong() && year % 100 != 0.toLong()) || year % 400 == 0.toLong()) {
                    isLeapYear = true
                }

                // checks for appropriate month
                if (month < 1 || month > 12) {
                    return false
                }

                // checks for appropriate day
                if (day < 1) return false

                if (month == 1.toLong() || month == 3.toLong() || month == 5.toLong()
                    || month == 7.toLong() || month == 8.toLong() || month == 10.toLong()
                    || month == 12.toLong()) {
                    if (day > 31) {
                        return false
                    }
                }
                else if (month != 2.toLong()) {
                    if (day > 30) {
                        return false
                    }
                }
                else if (isLeapYear) {
                    if (day > 29) {
                        return false
                    }
                }
                else if (day > 28) {
                    return false
                }

                // returns true if it passes all tests
                return true
            }

            // validates and sets date of last watering
            var lastWateringDateValidated = 0.toLong()

            // gets current date
            val currentDate = LocalDateTime.now()
            val simpleDateFormatter = DateTimeFormatter.BASIC_ISO_DATE
            val currentDateFormatted: Long = currentDate.format(simpleDateFormatter).toLong()

            try {
                val lastWateringDateAsString: String = lastWateringDate.text.toString()
                // ignores the dashes and slashes of date
                val wateringDateYear : String = lastWateringDateAsString.substring(0,4)
                val wateringDateMonth: String = lastWateringDateAsString.substring(5,7)
                val wateringDateDay: String = lastWateringDateAsString.substring(8,10)
                lastWateringDateValidated = (wateringDateYear + wateringDateMonth + wateringDateDay).toLong()

                // checks for a valid date
                if (!isValidDate(lastWateringDateValidated)) {
                    isValidForm = false
                    //Toast.makeText(this@new_plant_details, "\"Last Watering Date\" date is invalid.", Toast.LENGTH_SHORT).show()
                    Toast.makeText(this@NewPlantDetails, lastWateringDateValidated.toString(), Toast.LENGTH_SHORT).show()

                }

                if (lastWateringDateValidated > currentDateFormatted) {
                    // don't accept form if the date is in the future
                    isValidForm = false
                    Toast.makeText(this@NewPlantDetails, "\"Last Watering Date\" can't be in the future.", Toast.LENGTH_SHORT).show()
                }
            }
            catch(e: java.lang.Exception) {
                isValidForm = false
                Toast.makeText(this@NewPlantDetails, "\"Last Watering Date\" must be in form YYYY-mm-dd.", Toast.LENGTH_SHORT).show()
            }

            // validates and sets days per fertilizing
            var daysPerFertilizingLong = 0.toLong()

            try {
                daysPerFertilizingLong = daysPerFertilizing.text.toString().toLong()
                if (daysPerFertilizingLong < 1) {
                    // don't accept form if the days per watering is empty or less than 0
                    isValidForm = false
                    Toast.makeText(this@NewPlantDetails, "\"Days Per Fertilizing\" must be 1 or greater.", Toast.LENGTH_SHORT).show()
                }
            }
            catch(e:java.lang.NumberFormatException) {
                isValidForm = false
                Toast.makeText(this@NewPlantDetails, "\"Days Per Fertilizing\" must be a valid number.", Toast.LENGTH_SHORT).show()
            }

            // validates and sets date of last fertilizing
            var lastFertilizingDateValidated = 0.toLong()

            try {
                val lastFertilizingDateAsString: String = lastFertilizingDate.text.toString()
                // ignores the dashes and slashes of date
                val fertilizingDateYear : String = lastFertilizingDateAsString.substring(0,4)
                val fertilizingDateMonth: String = lastFertilizingDateAsString.substring(5,7)
                val fertilizingDateDay: String = lastFertilizingDateAsString.substring(8,10)
                lastFertilizingDateValidated = (fertilizingDateYear + fertilizingDateMonth + fertilizingDateDay).toLong()

                // checks for a valid date
                if (!isValidDate(lastFertilizingDateValidated)) {
                    isValidForm = false
                    Toast.makeText(this@NewPlantDetails, "\"Last Fertilizing Date\" date is invalid.", Toast.LENGTH_SHORT).show()
                }

                if (lastFertilizingDateValidated > currentDateFormatted) {
                    // don't accept form if the days per fertilizing is in the future
                    isValidForm = false
                    Toast.makeText(this@NewPlantDetails, "\"Last Fertilizing Date\" can't be in the future.", Toast.LENGTH_SHORT).show()
                }
            }
            catch(e: java.lang.Exception) {
                isValidForm = false
                Toast.makeText(this@NewPlantDetails, "\"Last Fertilizing Date\" must be in form YYYY-mm-dd.", Toast.LENGTH_SHORT).show()
            }

            // validates and sets date of harvest
            var harvestDateValidated: Long = 0
            val harvestDateAsString: String = harvestDate.text.toString()

            // only processes the date if one is specified
            if (harvestDateAsString.isNotEmpty()){
                try {
                    // ignores the dashes and slashes of date
                    val harvestDateYear: String = harvestDateAsString.substring(0, 4)
                    val harvestDateMonth: String = harvestDateAsString.substring(5, 7)
                    val harvestDateDay: String = harvestDateAsString.substring(8, 10)
                    harvestDateValidated =
                        (harvestDateYear + harvestDateMonth + harvestDateDay).toLong()

                    // checks for a valid date
                    if (!isValidDate(harvestDateValidated)) {
                        isValidForm = false
                        Toast.makeText(
                            this@NewPlantDetails,
                            "\"Harvest Date\" date is invalid.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    if (harvestDateValidated < currentDateFormatted) {
                        // don't accept form if the days per watering is empty or less than 0
                        isValidForm = false
                        Toast.makeText(
                            this@NewPlantDetails,
                            "\"Harvest Date\" can't be in the past.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                catch(e: java.lang.Exception) {
                    isValidForm = false
                    Toast.makeText(
                        this@NewPlantDetails,
                        "\"Harvest Date\" must be in form YYYY-mm-dd.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            // if all checks out, add plant and return to plant list
            if (isValidForm) {

                // creates a plant object
                val addedPlant = Plant(plantNameText,daysPerWateringLong,daysPerFertilizingLong,lastWateringDateValidated,lastFertilizingDateValidated,harvestDateValidated)
                // adds items to database
                if (uid != null) {
                    val plantKey = db.child(uid).child("plants").push().key
                    if (plantKey != null) {
                        addedPlant.plantKey = plantKey
                        db.child(uid).child("plants").child(plantKey).setValue(addedPlant).addOnSuccessListener {
                            // returns to plant list
                            startPlantList.putExtra("uid", uid)
                            startActivity(startPlantList)
                        }
                    }
                }

            }
        }
    }
}