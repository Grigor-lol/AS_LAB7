package com.example.stepsapp.ui.screens

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.DatePicker
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.stepsapp.R
import com.example.stepsapp.StepsTopAppBar
import com.example.stepsapp.ui.navigation.NavigationDestination
import com.example.stepsapp.viewmodel.AppViewModelProvider
import com.example.stepsapp.viewmodel.NewRecordViewModel
import com.example.stepsapp.viewmodel.RecordDetails
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar

object NewRecordDestination : NavigationDestination {
    override val route = "new_record"
    override val titleRes = R.string.record_entry_title
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewRecordScreen(
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    viewModel: NewRecordViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    Scaffold(
        topBar = {
            StepsTopAppBar(
                title = stringResource(NewRecordDestination.titleRes),
                canNavigateBack = true,
                navigateUp = onNavigateUp,
            )
        }
    ) { innerPadding ->
        EditBody(
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .fillMaxWidth(),
            navigateBack = navigateBack,
            onRecordValueChange = viewModel::updateUiState,
            onSaveClick = viewModel::insertRecord,
            recordDetails = viewModel.recordState
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditBody(modifier: Modifier = Modifier,
             navigateBack: () -> Unit,
             onRecordValueChange: (RecordDetails) -> Unit,
             onSaveClick: () -> Unit,
             recordDetails: RecordDetails
){
    val localContext = LocalContext.current
    Column(
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_large)),
        modifier = modifier.padding(dimensionResource(id = R.dimen.padding_medium))
    ) {
        BackHandler(onBack = {
            navigateBack()
        })

        val mContext = LocalContext.current

        // Declaring and initializing a calendar
        val mCalendar = Calendar.getInstance()
        val mYearPick = mCalendar.get(Calendar.YEAR)
        val mMonthPick = mCalendar.get(Calendar.MONTH)
        val mDayPick = mCalendar.get(Calendar.DAY_OF_MONTH)
        val mHourStart = mCalendar[Calendar.HOUR_OF_DAY]
        val mMinuteStart = mCalendar[Calendar.MINUTE]
        val mHourEnd = mCalendar[Calendar.HOUR_OF_DAY]
        val mMinuteEnd = mCalendar[Calendar.MINUTE]
        val instantStartDate = Calendar.getInstance()
        instantStartDate.set(recordDetails.startTime.year, recordDetails.startTime.monthValue-1,
            recordDetails.startTime.dayOfMonth)
        val instantEndDate = Calendar.getInstance()
        instantEndDate.set(recordDetails.endTime.year, recordDetails.endTime.monthValue-1,
            recordDetails.endTime.dayOfMonth)

        var startCheck by rememberSaveable{ mutableStateOf(false) }
        var endCheck by rememberSaveable{ mutableStateOf(false) }

        var recordDate by remember { mutableStateOf(recordDetails.startTime
            .format(DateTimeFormatter.ofPattern("yyyy:MM:dd"))) }
        var startTime by remember { mutableStateOf("") }
        var endTime by remember { mutableStateOf("") }

        var steps by rememberSaveable { mutableStateOf("") }
        val selectedStartDate by remember { mutableStateOf(instantStartDate) }
        val selectedEndDate by remember { mutableStateOf(instantEndDate) }

        var startError by rememberSaveable{ mutableStateOf(false) }
        var endError by rememberSaveable{ mutableStateOf(false) }
        var equalError by rememberSaveable{ mutableStateOf(false) }

        val mDatePickerDialog = DatePickerDialog(
            mContext,
            { _: DatePicker, mYear: Int, mMonth: Int, mDayOfMonth: Int ->
                recordDate = "$mYear:${if((mMonth+1) / 10 == 0) "0${mMonth+1}" 
                    else mMonth+1}:${if(mDayOfMonth / 10 == 0) "0$mDayOfMonth" else mDayOfMonth}"
                selectedStartDate.set(mYear, mMonth, mDayOfMonth)
                selectedEndDate.set(mYear, mMonth, mDayOfMonth)

                val newDate = ZonedDateTime.ofInstant(selectedStartDate.toInstant(), ZoneId.systemDefault())
                onRecordValueChange(recordDetails.copy(startTime = newDate))
            }, mYearPick, mMonthPick, mDayPick
        )
        mDatePickerDialog.datePicker.maxDate = mCalendar.timeInMillis

        val mTimeStartPickerDialog = TimePickerDialog(
            mContext,
            {_, mHour : Int, mMinute: Int ->
                startTime = "${if(mHour / 10 == 0) "0$mHour" else mHour}:${if(mMinute / 10 == 0) "0$mMinute" else mMinute}"
                selectedStartDate.set(Calendar.HOUR_OF_DAY, mHour)
                selectedStartDate.set(Calendar.MINUTE, mMinute)
                selectedStartDate.set(Calendar.SECOND, 0)
                startCheck = true
                val newDate = ZonedDateTime.ofInstant(selectedStartDate.toInstant(), ZoneId.systemDefault())
                onRecordValueChange(recordDetails.copy(startTime = newDate))
                if (endCheck && selectedStartDate.time.after(selectedEndDate.time)) {
                    startError = true
                    equalError = false
                    endError = false
                }
                else if (endCheck && selectedStartDate.time.equals(selectedEndDate.time)) {
                    equalError = true
                    startError = false
                    endError = false
                }
                else {
                    startError = false
                    endError = false
                    equalError = false
                }
            }, mHourStart, mMinuteStart, true
        )

        val mTimeEndPickerDialog = TimePickerDialog(
            mContext,
            {_, mHour : Int, mMinute: Int ->
                endTime = "${if(mHour / 10 == 0) "0$mHour" else mHour}:${if(mMinute / 10 == 0) "0$mMinute" else mMinute}"
                selectedEndDate.set(Calendar.HOUR_OF_DAY, mHour)
                selectedEndDate.set(Calendar.MINUTE, mMinute)
                selectedEndDate.set(Calendar.SECOND, 0)
                endCheck = true
                val newDate = ZonedDateTime.ofInstant(selectedEndDate.toInstant(), ZoneId.systemDefault())
                onRecordValueChange(recordDetails.copy(endTime = newDate))
                if (startCheck && selectedEndDate.time.before(selectedStartDate.time)) {
                    endError = true
                    startError = false
                    equalError = false
                }
                else if (startCheck && selectedStartDate.time.equals(selectedEndDate.time)) {
                    equalError = true
                    endError = false
                    startError = false
                }
                else {
                    endError = false
                    startError = false
                    equalError = false
                }
            }, mHourEnd, mMinuteEnd, true
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_small)),
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = recordDate,
                onValueChange = {
                    recordDate = it
                },
                label = { Text("Date") },
                modifier = Modifier.fillMaxWidth().clickable {
                    mDatePickerDialog.show()
                },
                leadingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.baseline_calendar_today_24),
                        contentDescription = "calendar"
                    )
                },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    disabledTextColor = Color.Blue,
                    disabledBorderColor = Color.Blue,
                    disabledPlaceholderColor = Color.Blue,
                    disabledLabelColor = Color.Blue,
                    //For Icons
                    disabledLeadingIconColor = Color.Blue,
                    disabledTrailingIconColor = Color.Blue,),
                enabled = false,
                singleLine = true
            )
            Row(verticalAlignment = Alignment.CenterVertically){
                OutlinedTextField(
                    value = startTime,
                    onValueChange = {
                        startTime = it
                    },
                    label = { Text("Start Time") },
                    modifier = Modifier.weight(1f).padding(start = 1.dp).clickable {
                        mTimeStartPickerDialog.show()
                    },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.baseline_access_time_24),
                            contentDescription = "clock"
                        )
                    },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        disabledTextColor = Color.Blue,
                        disabledBorderColor = Color.Blue,
                        disabledPlaceholderColor = Color.Blue,
                        disabledLabelColor = Color.Blue,
                        //For Icons
                        disabledLeadingIconColor = Color.Blue,
                        disabledTrailingIconColor = Color.Blue,),
                    enabled = false,
                    singleLine = true,
                    isError = startError || equalError,
                    supportingText = {
                        if(startError) {
                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                text = "Start time after end",
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                        else if (equalError){
                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                text = "Equal times",
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                        else if (endError) {
                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                text = "",
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                )

                OutlinedTextField(
                    value = endTime,
                    onValueChange = {
                        endTime = it
                    },
                    label = { Text("End Time") },
                    modifier = Modifier.weight(1f).padding(start = 4.dp).clickable {
                        mTimeEndPickerDialog.show()
                    },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.baseline_access_time_24),
                            contentDescription = "clock"
                        )
                    },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        disabledTextColor = Color.Blue,
                        disabledBorderColor = Color.Blue,
                        disabledPlaceholderColor = Color.Blue,
                        disabledLabelColor = Color.Blue,
                        //For Icons
                        disabledLeadingIconColor = Color.Blue,
                        disabledTrailingIconColor = Color.Blue,),
                    enabled = false,
                    singleLine = true,
                    isError = endError || equalError,
                    supportingText = {
                        if(endError) {
                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                text = "End time before start",
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                        else if (equalError){
                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                text = "Equal times",
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                        else if (startError) {
                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                text = "",
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                )
            }
            Text("Steps count")
            OutlinedTextField(
                value = steps,
                onValueChange = {
                    if (Regex("^\\d*\$").matches(it)){
                        steps = it
                        onRecordValueChange(recordDetails.copy(steps = it.toInt()))
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    disabledTextColor = Color.Blue,
                    disabledBorderColor = Color.Blue,
                    disabledPlaceholderColor = Color.Blue,
                    disabledLabelColor = Color.Blue,
                    //For Icons
                    disabledLeadingIconColor = Color.Blue,
                    disabledTrailingIconColor = Color.Blue,),
                enabled = true,
                singleLine = true
            )
        }
        Spacer(Modifier.height(250.dp))
        Button(
            onClick = {
                onSaveClick()
                navigateBack()
                Toast.makeText(localContext, "New record was added!", Toast.LENGTH_SHORT).show()
            },
            enabled = startCheck && endCheck && !startError && !endError && !equalError,
            shape = CircleShape,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = stringResource(R.string.record_entry_title),
                tint = Color.Gray
            )
        }
    }
}