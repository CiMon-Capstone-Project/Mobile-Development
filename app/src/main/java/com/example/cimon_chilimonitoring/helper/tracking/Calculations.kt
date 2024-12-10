package com.example.cimon_chilimonitoring.helper.tracking

import java.sql.Date
import java.sql.Timestamp
import java.text.SimpleDateFormat

object Calculations {
    // calculate day after start

    fun calculateTimeBetweenDates(startDate: String): String {

        val endDate = timeStampToString(System.currentTimeMillis())

        val sdf = SimpleDateFormat("dd/MM/yyyy")
        val date1 = sdf.parse(startDate)
        val date2 = sdf.parse(endDate)

        var isNegative = false

        var difference = date2.time - date1.time
        if (difference < 0) {
            difference = -(difference)
            isNegative = true
        }

//        val minutes = difference / 60 / 1000
//        val hours = difference / 60 / 1000 / 60
        val days = (difference / 60 / 1000 / 60) / 24
        val months = (difference / 60 / 1000 / 60) / 24 / (365 / 12)
        val years = difference / 60 / 1000 / 60 / 24 / 365

        if (isNegative) {

            return when {
                days < 61 -> if (days < 10) "0$days" else "$days"
                months < 24 -> if (months < 10) "0$months" else "$months"
                else -> if (years < 10) "0$years" else "$years"
            }
        }

        return when {
            days < 61 -> if (days < 10) "0$days" else "$days"
            months < 24 -> if (months < 10) "0$months" else "$months"
            else -> if (years < 10) "0$years" else "$years"
        }
    }

    fun textCalculateTimeBetweenDates(startDate: String): String {

        val endDate = timeStampToString(System.currentTimeMillis())

        val sdf = SimpleDateFormat("dd/MM/yyyy")
        val date1 = sdf.parse(startDate)
        val date2 = sdf.parse(endDate)

        var isNegative = false

        var difference = date2.time - date1.time
        if (difference < 0) {
            difference = -(difference)
            isNegative = true
        }

//        val minutes = difference / 60 / 1000
//        val hours = difference / 60 / 1000 / 60
        val days = (difference / 60 / 1000 / 60) / 24
        val months = (difference / 60 / 1000 / 60) / 24 / (365 / 12)
        val years = difference / 60 / 1000 / 60 / 24 / 365

        if (isNegative) {

            return when {
//                minutes < 240 -> "Starts in $minutes minutes"
//                hours < 48 -> "Starts in $hours hours"
                days < 61 -> "Hari Lagi"
                months < 24 -> "Bulan Lagi"
                else -> "Tahun Lagi"
            }
        }

        return when {
//            minutes < 240 -> "$minutes minutes ago"
//            hours < 48 -> "$hours hours ago"
            days < 61 -> "Hari Berlalu"
            months < 24 -> "Bulan Berlalu"
            else -> "Tahun Terlewati"
        }
    }


    private fun timeStampToString(timeStamp: Long): String {
        val stamp = Timestamp(timeStamp)
        val sdf = SimpleDateFormat("dd/MM/yyyy")
        val date:String = sdf.format((Date(stamp.time)))

        return date.toString()
    }

    fun cleanDate(_day: Int, _month: Int, _year: Int): String {
        var day = _day.toString()
        var month = _month.toString()

        if (_day < 10) {
            day = "0$_day"
        }

        if (_month < 9) { //Because the month instance we retrieve starts at 0 and it's stupid!
            month = "0${_month + 1}"
        } else if (_month >= 9 && _month <= 11) {
            month = (_month + 1).toString()
        }

        return "$day/$month/$_year"
    }

    fun cleanTime(_hour: Int, _minute: Int): String {
        var hour = _hour.toString()
        var minute = _minute.toString()

        if (_hour < 10) {
            hour = "0$_hour"
        }
        if (_minute < 10) {
            minute = "0$_minute"
        }
        return "$hour:$minute"
    }
}