package com.example.plantbuddy

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class PlantListAdapter(private val plantList: MutableList<Plant>, // context for starting new activity
                       private val context: Context,
                       private val uid: String
) :
    RecyclerView.Adapter<PlantListAdapter.PlantViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlantViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.plant_item, parent
        , false)
        return PlantViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        // lets the adapter know how many items are in the recycler view
        return plantList.size
    }

    override fun onBindViewHolder(holder: PlantViewHolder, position: Int) {
        val currentPlant = plantList[position]

        // grabs today's date
        val currentDate = LocalDateTime.now()
        val simpleDateFormatter = DateTimeFormatter.BASIC_ISO_DATE
        val currentDateFormatted: Int = currentDate.format(simpleDateFormatter).toInt()

        holder.plantName.text = currentPlant.plantName

        // only displays alerts if they are due
        if (currentDateFormatted < currentPlant.nextWateringDate) {
            holder.wateringAlert.text = ""
        }
        if (currentDateFormatted < currentPlant.nextFertilizingDate) {
            holder.fertilizingAlert.text = ""
        }
        if (currentPlant.harvestDate == 0.toLong() || currentPlant.harvestDate > currentDateFormatted) {
            holder.harvestAlert.text = ""
        }

        // listens for deletion and deletes item
        holder.deleteButton.setOnClickListener {
            // gets reference to users in the database
            val db: DatabaseReference = FirebaseDatabase.getInstance().getReference("Users").child(uid).child("plants").child(currentPlant.plantKey)
            db.removeValue().addOnSuccessListener {
                val startPlantList = Intent(this.context,MainActivity::class.java)
                startPlantList.putExtra("uid", uid)
                this.context.startActivity(startPlantList)
            }
        }

        // listens for update button click and takes user to update activity
        holder.updateButton.setOnClickListener {
            val updatePlant = Intent(this.context,UpdatePlant::class.java)
            updatePlant.putExtra("uid", uid)
            updatePlant.putExtra("plantKey", currentPlant.plantKey)
            updatePlant.putExtra("plantName", currentPlant.plantName)
            updatePlant.putExtra("harvestDate", currentPlant.harvestDate)
            this.context.startActivity(updatePlant)
        }
    }

    class PlantViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val plantName : TextView = itemView.findViewById(R.id.plant_name)
        val wateringAlert : TextView = itemView.findViewById(R.id.watering_alert)
        val fertilizingAlert : TextView = itemView.findViewById(R.id.fertilizing_alert)
        val harvestAlert : TextView = itemView.findViewById(R.id.harvest_alert)
        val deleteButton : Button = itemView.findViewById(R.id.deleteButton)
        val updateButton : Button = itemView.findViewById(R.id.updateButton)
    }
}