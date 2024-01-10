package com.example.stepsapp.viewmodel

import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.health.connect.client.records.StepsRecord
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stepsapp.HealthConnectManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

class MainScreenViewModel(private val healthConnectManager: HealthConnectManager): ViewModel() {

    private val _recordsUiState = MutableStateFlow(RecordsUiState())
    val recordsUiState = _recordsUiState.asStateFlow()

    var requestedRecordState by mutableStateOf(RequestedRecord())
        private set

    fun updateRequestedRecordState(requestedRecordDetails: RequestedRecord) {
        requestedRecordState = requestedRecordDetails
    }

    fun readDayRecords(requestPermissions: ActivityResultLauncher<Set<String>>) {

        val startOfNextDay = requestedRecordState.selectedDay.plusDays(1)
        viewModelScope.launch {
            healthConnectManager.checkPermissionsAndRun(requestPermissions)

            try{
                _recordsUiState.update {
                    it.copy(recordsList = healthConnectManager
                        .readRecords(requestedRecordState.selectedDay, startOfNextDay))
                }

                if (recordsUiState.value.recordsList.isNotEmpty()){
                    val total = healthConnectManager.getTotalSteps(requestedRecordState.selectedDay, startOfNextDay)
                    updateRequestedRecordState(requestedRecordState.copy(stepsOverall = total))
                }
            } catch (securityException: SecurityException){
                Log.d("d-error", "no permissions")
            }
        }
    }

    fun deleteRecord(startTime: ZonedDateTime, endTime: ZonedDateTime){
        val startOfNextDay = requestedRecordState.selectedDay.plusDays(1)
        viewModelScope.launch {
            healthConnectManager.deleteStepsByTimeRange(startTime,endTime)

            _recordsUiState.update {
                it.copy(recordsList = healthConnectManager
                    .readRecords(requestedRecordState.selectedDay, startOfNextDay))
            }

            if (recordsUiState.value.recordsList.isNotEmpty()){
                val total = healthConnectManager.getTotalSteps(requestedRecordState.selectedDay, startOfNextDay)
                updateRequestedRecordState(requestedRecordState.copy(stepsOverall = total))
            }
        }
    }
}

data class RecordsUiState(val recordsList: List<StepsRecord> = listOf())

data class RequestedRecord(
    val selectedDay: ZonedDateTime = ZonedDateTime.now().truncatedTo(ChronoUnit.DAYS),
    val stepsOverall: Long = 0
)