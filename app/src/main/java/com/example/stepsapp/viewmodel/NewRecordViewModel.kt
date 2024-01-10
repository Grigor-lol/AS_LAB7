package com.example.stepsapp.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stepsapp.HealthConnectManager
import com.example.stepsapp.SharedData.selectedSharedDay
import kotlinx.coroutines.launch
import java.time.ZonedDateTime

class NewRecordViewModel(private val healthConnectManager: HealthConnectManager): ViewModel() {
    var recordState by mutableStateOf(RecordDetails(
        startTime = selectedSharedDay, endTime = selectedSharedDay
    ))
        private set

    fun updateUiState(recordDetails: RecordDetails) {
        recordState = recordDetails
    }

    fun insertRecord() {
        Log.d("test-ins", "${recordState.startTime} || ${recordState.endTime}")
        viewModelScope.launch {
            healthConnectManager.writeSteps(recordState.startTime, recordState.endTime, recordState.steps.toLong())
        }
    }
}

data class RecordDetails(
    val startTime: ZonedDateTime = ZonedDateTime.now(),
    val endTime: ZonedDateTime = ZonedDateTime.now(),
    val steps: Int = 0
)