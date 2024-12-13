package com.example.cimon_chilimonitoring.ui.tracking.create

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.DatePicker
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.cimon_chilimonitoring.data.local.entity.TrackingEntity
import com.example.cimon_chilimonitoring.data.local.repository.TrackingRepo
import com.example.cimon_chilimonitoring.data.local.room.HistoryDatabase
import com.example.cimon_chilimonitoring.databinding.ActivityCreateTrackingBinding
import com.example.cimon_chilimonitoring.helper.tracking.Calculations
import com.example.cimon_chilimonitoring.ui.tracking.TrackingViewModel
import com.example.cimon_chilimonitoring.ui.tracking.TrackingViewModelFactory
import java.util.Calendar

class CreateTrackingActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener {
    private lateinit var binding: ActivityCreateTrackingBinding

    private var title= ""
    private var description= ""
    private var timeStamp = ""

    private var day = 0
    private var month = 0
    private var year = 0

    private var cleanDate = ""

    private lateinit var trackingViewModel: TrackingViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCreateTrackingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val reminderRepository = TrackingRepo(HistoryDatabase.getInstance(this))
        val viewModelProviderFactory = TrackingViewModelFactory(application, reminderRepository)
        trackingViewModel = ViewModelProvider(this, viewModelProviderFactory).get(TrackingViewModel::class.java)


        binding.topAppBar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        with(binding){
            btnCreate.setOnClickListener{
                addTrackingToDB()
            }

            pickDateAndTime()
        }
    }

    //set on click listeners for our data and time pickers
    private fun pickDateAndTime() {
        binding.btnDate.setOnClickListener {
            getDateCalendar()
            DatePickerDialog(this, this, year, month, day).show()
        }
    }

    private fun addTrackingToDB() {
        //Get text from editTexts
        title = binding.taTitle.text.toString()
        description = binding.taDescription.text.toString()

        //Create a timestamp string for our recyclerview
        timeStamp = cleanDate

        //Check that the form is complete before submitting data to the database
        if (!(title.isEmpty() || description.isEmpty() || timeStamp.isEmpty())) {
            val tracking = TrackingEntity(0, title, description, timeStamp)

            //add the habit if all the fields are filled
            trackingViewModel.addTracking(tracking)
            Toast.makeText(this, "Tracking berhasil ditambahkan!", Toast.LENGTH_SHORT).show()

            val resultIntent = Intent()
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        } else {
            Toast.makeText(this, "Mohon isi seluruh data", Toast.LENGTH_SHORT).show()
        }
    }

    //get the date set
    override fun onDateSet(p0: DatePicker?, yearX: Int, monthX: Int, dayX: Int) {
        cleanDate = Calculations.cleanDate(dayX, monthX, yearX)
        binding.taDate.setText("$cleanDate")
    }

    //get the current date
    private fun getDateCalendar() {
        val cal = Calendar.getInstance()
        day = cal.get(Calendar.DAY_OF_MONTH)
        month = cal.get(Calendar.MONTH)
        year = cal.get(Calendar.YEAR)
    }
}