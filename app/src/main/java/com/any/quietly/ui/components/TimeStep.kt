package com.any.quietly.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.util.Calendar
import java.util.Locale

private const val QUIET_WINDOW_DURATION_HOURS = 12

private fun buildStartCalendar(hour: Int, minute: Int): Calendar {
    val now = Calendar.getInstance()
    val nowTruncatedToMinute = (now.clone() as Calendar).apply {
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    val startCalendar = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, hour)
        set(Calendar.MINUTE, minute)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    if (startCalendar.before(nowTruncatedToMinute)) {
        startCalendar.add(Calendar.DAY_OF_YEAR, 1)
    }
    return startCalendar
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeStep(
    onNextClick: (startTime: Long, endTime: Long) -> Unit,
    onBackClick: () -> Unit
) {
    val calendar = Calendar.getInstance()
    val startTimePickerState = rememberTimePickerState(
        initialHour = calendar.get(Calendar.HOUR_OF_DAY),
        initialMinute = calendar.get(Calendar.MINUTE)
    )

    val endTime by remember {
        derivedStateOf {
            val startCalendar = buildStartCalendar(
                hour = startTimePickerState.hour,
                minute = startTimePickerState.minute
            )
            val endCalendar = startCalendar.clone() as Calendar
            endCalendar.add(Calendar.HOUR_OF_DAY, QUIET_WINDOW_DURATION_HOURS)
            endCalendar
        }
    }
    val startsTomorrow by remember {
        derivedStateOf {
            val now = Calendar.getInstance()
            val startCalendar = buildStartCalendar(
                hour = startTimePickerState.hour,
                minute = startTimePickerState.minute
            )
            now.get(Calendar.DAY_OF_YEAR) != startCalendar.get(Calendar.DAY_OF_YEAR)
                    || now.get(Calendar.YEAR) != startCalendar.get(Calendar.YEAR)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Select a start time for your Quiet Window.")
        Spacer(modifier = Modifier.height(16.dp))
        TimePicker(state = startTimePickerState)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = String.format(
                Locale.getDefault(),
                "End Time: %02d:%02d",
                endTime.get(Calendar.HOUR_OF_DAY),
                endTime.get(Calendar.MINUTE)
            )
        )
        if (startsTomorrow) {
            Spacer(modifier = Modifier.height(4.dp))
            Text("Start time rolls to tomorrow.")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(onClick = onBackClick) {
                Text("Back")
            }
            Button(
                onClick = {
                    val startCalendar = buildStartCalendar(
                        hour = startTimePickerState.hour,
                        minute = startTimePickerState.minute
                    )
                    onNextClick(startCalendar.timeInMillis, endTime.timeInMillis)
                }
            ) {
                Text("Next")
            }
        }
    }
}
