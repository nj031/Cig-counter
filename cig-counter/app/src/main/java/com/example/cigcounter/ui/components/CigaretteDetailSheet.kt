package com.example.cigcounter.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.cigcounter.viewmodel.TimelineItemUi
import java.time.Instant
import java.time.LocalTime
import java.time.ZoneId

/**
 * Edits only the time-of-day (hour:minute) of the entry, keeping its
 * original calendar date — matches the "[Edit Time]" affordance in the
 * spec. The Repository's updateTimestamp() already recomputes `date` from
 * whatever timestamp it's given, so a future full date+time editor would
 * correctly handle a midnight-crossing edit without any Repository changes.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CigaretteDetailSheet(
    item: TimelineItemUi,
    onEditConfirm: (Long) -> Unit,
    onDelete: () -> Unit,
    onDismiss: () -> Unit
) {
    var showTimePicker by remember { mutableStateOf(false) }

    Column(modifier = Modifier.padding(24.dp)) {
        Text(
            text = "Cigarette #${item.number}",
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = item.timeLabel, style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Gap since previous: ${item.gapLabel}",
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = { showTimePicker = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Edit Time")
        }
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedButton(
            onClick = onDelete,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.error
            )
        ) {
            Text("Delete")
        }
        Spacer(modifier = Modifier.height(8.dp))
    }

    if (showTimePicker) {
        val zone = ZoneId.systemDefault()
        val originalDateTime = Instant.ofEpochMilli(item.timestamp).atZone(zone)
        val timePickerState = rememberTimePickerState(
            initialHour = originalDateTime.hour,
            initialMinute = originalDateTime.minute,
            is24Hour = false
        )
        Dialog(onDismissRequest = { showTimePicker = false }) {
            Surface(shape = MaterialTheme.shapes.large) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    TimePicker(state = timePickerState)
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        horizontalArrangement = Arrangement.End,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        TextButton(onClick = { showTimePicker = false }) {
                            Text("Cancel")
                        }
                        TextButton(onClick = {
                            val newDateTime = originalDateTime.toLocalDate()
                                .atTime(LocalTime.of(timePickerState.hour, timePickerState.minute))
                                .atZone(zone)
                            onEditConfirm(newDateTime.toInstant().toEpochMilli())
                            showTimePicker = false
                            onDismiss()
                        }) {
                            Text("Save")
                        }
                    }
                }
            }
        }
    }
}
