package com.any.quietly.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun NotificationCountStep(
    onNextClick: (Int) -> Unit,
    onBackClick: () -> Unit
) {
    val presets = listOf(5, 10, 50, 100)
    var selectedPreset by remember { mutableStateOf(10) }
    var customCount by remember { mutableStateOf("10") }

    val parsedCustom = customCount.toIntOrNull()
    val resolvedCount = parsedCustom?.takeIf { it > 0 } ?: selectedPreset

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Summarize after this many notifications")
        Spacer(modifier = Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            presets.forEach { preset ->
                FilterChip(
                    selected = selectedPreset == preset && customCount == preset.toString(),
                    onClick = {
                        selectedPreset = preset
                        customCount = preset.toString()
                    },
                    label = { Text(preset.toString()) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = customCount,
            onValueChange = { input ->
                if (input.all(Char::isDigit)) {
                    customCount = input
                }
            },
            label = { Text("Custom number") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        if (parsedCustom == null || parsedCustom <= 0) {
            Spacer(modifier = Modifier.height(8.dp))
            Text("Enter a number greater than 0.")
        }

        Spacer(modifier = Modifier.height(16.dp))
        Row {
            Button(onClick = onBackClick) {
                Text("Back")
            }
            Spacer(modifier = Modifier.width(16.dp))
            Button(
                onClick = { onNextClick(resolvedCount) },
                enabled = resolvedCount > 0
            ) {
                Text("Next")
            }
        }
    }
}
