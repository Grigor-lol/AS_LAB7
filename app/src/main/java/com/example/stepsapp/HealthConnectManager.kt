/*
 * Copyright 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.stepsapp

import android.content.Context
import androidx.activity.result.ActivityResultLauncher
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.request.AggregateRequest
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import java.time.ZonedDateTime

/**
 * Demonstrates reading and writing from Health Connect.
 */
class HealthConnectManager(private val context: Context) {
  private val healthConnectClient by lazy { HealthConnectClient.getOrCreate(context) }

  // Create a set of permissions for required data types
  companion object {
    val PERMISSIONS =
      setOf(
        HealthPermission.getReadPermission(StepsRecord::class),
        HealthPermission.getWritePermission(StepsRecord::class)
      )
  }


  suspend fun checkPermissionsAndRun(requestPermissions: ActivityResultLauncher<Set<String>>) {
    val granted = healthConnectClient.permissionController.getGrantedPermissions()
    if (granted.containsAll(PERMISSIONS)) {
      // Permissions already granted; proceed with inserting or reading data
    } else {
      requestPermissions.launch(PERMISSIONS)
    }
  }

  suspend fun getTotalSteps(start: ZonedDateTime, end: ZonedDateTime): Long {
    val response = healthConnectClient.aggregate(
      AggregateRequest(
        metrics = setOf(StepsRecord.COUNT_TOTAL),
        timeRangeFilter = TimeRangeFilter.between(start.toInstant(), end.toInstant())
      )
    )
    // The result may be null if no data is available in the time range
    return response[StepsRecord.COUNT_TOTAL]!!
  }

  suspend fun readRecords(start: ZonedDateTime, end: ZonedDateTime): List<StepsRecord> {
    val response = healthConnectClient.readRecords(
      ReadRecordsRequest(
        StepsRecord::class,
        timeRangeFilter = TimeRangeFilter.between(start.toInstant(), end.toInstant())
      )
    )
    return response.records
  }

  suspend fun deleteStepsByTimeRange(startTime: ZonedDateTime, endTime: ZonedDateTime) {
    healthConnectClient.deleteRecords(
      StepsRecord::class,
      timeRangeFilter = TimeRangeFilter.between(startTime.toInstant(), endTime.toInstant())
    )
  }

  suspend fun writeSteps(start: ZonedDateTime, end: ZonedDateTime, count: Long) {

    healthConnectClient.insertRecords(
      listOf(
        StepsRecord(
          startTime = start.toInstant(),
          startZoneOffset = start.offset,
          endTime = end.toInstant(),
          endZoneOffset = end.offset,
          count = count
        )
      )
    )
  }
}
