package com.any.quietly.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.any.quietly.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateQuietWindowBottomSheet(
    isVisible: Boolean,
    onDismissRequest: () -> Unit,
    onSaveClick: () -> Unit
) {
    if (isVisible) {
        ModalBottomSheet(
            onDismissRequest = onDismissRequest,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.create_quiet_window_title),
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(stringResource(id = R.string.pick_time_text))
                // TODO: Add actual TimePicker

                Spacer(modifier = Modifier.height(16.dp))

                Text(stringResource(id = R.string.select_apps_text))
                // TODO: Add AppPickerScreen

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onSaveClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(id = R.string.save_button_text))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun CreateQuietWindowBottomSheetPreview() {
    CreateQuietWindowBottomSheet(
        isVisible = true,
        onDismissRequest = { /* Do nothing for preview */ },
        onSaveClick = { /* Do nothing for preview */ }
    )
}
