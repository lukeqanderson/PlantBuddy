package com.example.plantbuddy

class Plant {
    // member variables for each plant
    var plantKey: String = ""
    var plantName: String = ""
    var daysPerWatering: Long = 0
    var daysPerFertilizing : Long = 0
    var lastWateringDate: Long = 0
    var lastFertilizingDate: Long = 0
    var nextWateringDate: Long = 0
    var nextFertilizingDate: Long = 0
    var harvestDate: Long = 0

    // constructor for empty class
    constructor()

    // constructor to initialize the class with attributes
    constructor(plantName: String, daysPerWatering: Long, daysPerFertilizing: Long,
    lastWateringDate: Long, lastFertilizingDate: Long, harvestDate: Long) {
        this.plantName = plantName
        this.daysPerWatering = daysPerWatering
        this.daysPerFertilizing = daysPerFertilizing
        this.lastWateringDate = lastWateringDate
        this.lastFertilizingDate = lastFertilizingDate
        this.harvestDate = harvestDate
    }

    // method to determine if a year is a leap year
    private fun isLeapYear(year: Long) : Boolean {
        return (year % 4 == 0.toLong() && year % 100 != 0.toLong()) || year % 400 == 0.toLong()
    }

    // // method to determine days in a month
    private fun daysInMonth(month: Long, year: Long) : Long {
        if ((month == 1.toLong()) || (month == 3.toLong()) || (month == 5.toLong())
            || (month == 7.toLong()) || (month == 8.toLong()) || (month == 10.toLong())
            || (month == 12.toLong())
        ) {
            return 31
        }
        if (month == 2.toLong()) {
            return if (this.isLeapYear(year)) {
                29
            } else 28
        }
        return 30
    }

    // function to calculate new date after a certain amount of days
    private fun calculateDateWithAddedDays(date: Long, days: Long): Long {

        // makes date and days mutable
        var tempDate = date
        var tempDays = days

        // strips away the year, day, and month
        var day: Long = tempDate % 100
        tempDate /= 100
        var month: Long = tempDate % 100
        tempDate /= 100
        var year: Long = tempDate

        while (tempDays != 0.toLong()) {

            // adds a year if future date will be past december
            if (month == 12.toLong() && day + tempDays > this.daysInMonth(month, year)) {
                // readjusts days for 1st of january next year
                tempDays -= (this.daysInMonth(month, year) - day + 1)
                year ++ //update current year to next year
                month = 1 // update to january
                day = 1 // update to first day
            }
            // updates to next month
            else if (day + tempDays > this.daysInMonth(month, year)) {
                tempDays -= (this.daysInMonth(month, year) - day + 1) // readjusts days
                month++ // updates to next month
                day = 1 // update to first day
            }
            else {
                day += tempDays
                tempDays = 0
            }
        }
        // reformats back to string for valid return
        var returnedTimestamp = ""
        returnedTimestamp += year.toString()
        returnedTimestamp += if (month.toString().length == 1) {
            ("0$month")
        } else {
            month.toString()
        }
        returnedTimestamp += if (day.toString().length == 1) {
            ("0$day")
        } else {
            day.toString()
        }
        return returnedTimestamp.toLong()
    }

    fun updateNextWateringDate() {
        this.nextWateringDate = calculateDateWithAddedDays(this.lastWateringDate,this.daysPerWatering)
    }

    fun updateNextFertilizingDate() {
        this.nextFertilizingDate = calculateDateWithAddedDays(this.lastFertilizingDate,this.daysPerFertilizing)
    }
}