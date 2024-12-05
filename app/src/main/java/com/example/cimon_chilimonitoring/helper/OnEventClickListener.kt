package com.example.cimon_chilimonitoring.helper

import com.example.cimon_chilimonitoring.data.remote.response.ListStory
import com.example.cimon_chilimonitoring.data.remote.response.ResultsItem

interface OnEventClickListener {
    fun onEventClick(event: ResultsItem)
}