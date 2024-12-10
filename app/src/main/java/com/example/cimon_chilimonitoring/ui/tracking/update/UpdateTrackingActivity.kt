package com.example.cimon_chilimonitoring.ui.tracking.update

import android.app.Activity
import android.app.ActivityOptions
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.DatePicker
import android.widget.TimePicker
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.navArgs
import com.example.cimon_chilimonitoring.MainActivity
import com.example.cimon_chilimonitoring.R
import com.example.cimon_chilimonitoring.data.local.entity.TrackingEntity
import com.example.cimon_chilimonitoring.data.local.repository.TrackingRepo
import com.example.cimon_chilimonitoring.data.local.room.HistoryDatabase
import com.example.cimon_chilimonitoring.databinding.ActivityUpdateTrackingBinding
import com.example.cimon_chilimonitoring.helper.tracking.Calculations
import com.example.cimon_chilimonitoring.ui.tracking.TrackingViewModel
import com.example.cimon_chilimonitoring.ui.tracking.TrackingViewModelFactory
import java.util.Calendar

class UpdateTrackingActivity : AppCompatActivity(), TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener  {
    private lateinit var binding: ActivityUpdateTrackingBinding

    private var title= ""
    private var description= ""
    private var timeStamp = ""

    private var day = 0
    private var month = 0
    private var year = 0
    private var hour = 0
    private var minute = 0

    private var cleanDate = ""
    private var cleanTime = ""
    private var idTracking = 0

    private lateinit var trackingViewModel: TrackingViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityUpdateTrackingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.topAppBar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val reminderRepository = TrackingRepo(HistoryDatabase.getInstance(this))
        val viewModelProviderFactory = TrackingViewModelFactory(application, reminderRepository)
        trackingViewModel = ViewModelProvider(this, viewModelProviderFactory).get(TrackingViewModel::class.java)

        updateUI()
        Log.d("UpdateTrackingActivity", "upateTrackingToDB: $title, $description, $timeStamp")
        with(binding){
            pickDateAndTime()

            btnCreate.setOnClickListener{
                upateTrackingToDB()
            }

            btnDelete.setOnClickListener {
                deleteTracking()
            }
        }
    }

    private fun upateTrackingToDB() {
        title = binding.taTitle.text.toString()
        description = binding.taDescription.text.toString()

        timeStamp = if (cleanDate.isEmpty()) binding.taDate.text.toString() else cleanDate

        try {
            if (!(title.isEmpty() || description.isEmpty() || timeStamp.isEmpty())) {
                val tracking = TrackingEntity(idTracking, title, description, timeStamp)

                // Update the habit if all the fields are filled
                trackingViewModel.updateTracking(tracking)
                Log.d("UpdateTrackingActivity", "upateTrackingToDB: $tracking")
                Toast.makeText(this, "Tracking berhasil diperbarui!", Toast.LENGTH_SHORT).show()
                val intent = Intent(this@UpdateTrackingActivity, MainActivity::class.java).apply {
                    putExtra("navigateTo", R.id.navigation_tracking)
                }
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this@UpdateTrackingActivity).toBundle())
            } else {
                Toast.makeText(this, "Mohon isi seluruh data", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Mohon isi seluruh data", Toast.LENGTH_SHORT).show()
        }
    }

    private fun deleteTracking() {
        val tracking = TrackingEntity(idTracking, title, description, timeStamp)
        trackingViewModel.deleteReminder(tracking)

        Toast.makeText(this, "Tracking $title dihapus!", Toast.LENGTH_SHORT).show()
        val intent = Intent(this@UpdateTrackingActivity, MainActivity::class.java).apply {
            putExtra("navigateTo", R.id.navigation_tracking)
        }
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this@UpdateTrackingActivity).toBundle())
    }

    private fun updateUI(){
        val trackingList = intent.getSerializableExtra("STORY_OBJECT") as ArrayList<TrackingEntity>
        val tracking = trackingList[0]
        tracking.let {
            with(binding){
                idTracking = it.id
                timeStamp = it.startTime
                Log.d("qweqeqe", "updateUI: $timeStamp")
                title = it.title
                description = it.description

                taTitle.setText(it.title)
                taDescription.setText(it.description)
                taDate.setText(it.startTime)
            }
        }
    }

    private fun pickDateAndTime() {
        binding.btnDate.setOnClickListener {
            getDateCalendar()
            DatePickerDialog(this, this, year, month, day).show()
        }
    }

    //get the current date
    private fun getDateCalendar() {
        val cal = Calendar.getInstance()
        day = cal.get(Calendar.DAY_OF_MONTH)
        month = cal.get(Calendar.MONTH)
        year = cal.get(Calendar.YEAR)
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        TODO("Not yet implemented")
    }

    override fun onDateSet(p0: DatePicker?, yearX: Int, monthX: Int, dayX: Int) {
        cleanDate = Calculations.cleanDate(dayX, monthX, yearX)
        binding.taDate.setText("$cleanDate")
    }
}