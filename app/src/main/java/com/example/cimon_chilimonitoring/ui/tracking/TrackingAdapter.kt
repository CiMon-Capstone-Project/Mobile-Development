package com.example.cimon_chilimonitoring.ui.tracking

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.cimon_chilimonitoring.data.local.entity.TrackingEntity
import com.example.cimon_chilimonitoring.databinding.ItemCardViewTrackingBinding
import com.example.cimon_chilimonitoring.helper.tracking.Calculations
import com.example.cimon_chilimonitoring.ui.tracking.TrackingFragment.Companion.REQUEST_UPDATE_TRACKING
import com.example.cimon_chilimonitoring.ui.tracking.update.UpdateTrackingActivity
import java.io.Serializable

class TrackingAdapter : RecyclerView.Adapter<TrackingAdapter.MyViewHolder>() {
    var trackingList = emptyList<TrackingEntity>()

    inner class MyViewHolder(val itemBinding: ItemCardViewTrackingBinding) : RecyclerView.ViewHolder(itemBinding.root){
        init {
            itemBinding.root.setOnClickListener {
                val position = adapterPosition
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            ItemCardViewTrackingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: TrackingAdapter.MyViewHolder, position: Int) {
        val currentTracking = trackingList[position]

        holder.itemBinding.apply {
            tvTitle.text = currentTracking.title
            tvTrackingDescription.text = currentTracking.description
            tvDayElapsed.text = Calculations.calculateTimeBetweenDates(currentTracking.startTime.trim())
            tvTrackingStartDateInput.text = currentTracking.startTime
            tvDayPassed.text = Calculations.textCalculateTimeBetweenDates(currentTracking.startTime.trim())

            holder.itemView.setOnClickListener{
                val intent = Intent(holder.itemView.context, UpdateTrackingActivity::class.java).apply {
                    putExtra("STORY_OBJECT", trackingList as Serializable)
                }
                (holder.itemView.context as Activity).startActivityForResult(intent, REQUEST_UPDATE_TRACKING, ActivityOptionsCompat.makeSceneTransitionAnimation(holder.itemView.context as Activity).toBundle())
            }
        }
    }

    override fun getItemCount(): Int {
        return trackingList.size
    }

    fun setData(habit: List<TrackingEntity>) {
        this.trackingList = habit
        notifyDataSetChanged()
    }
}