package com.qiuqiuqiu.weatherPredicate.ui.normal

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SearchTextBox(
    label: String,
    input: String,
    inputChanged: (String) -> Unit,
    onClear: (() -> Unit),
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier, shape = RoundedCornerShape(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxSize()) {
            Icon(
                imageVector = Icons.Default.Search,
                null,
                modifier = Modifier
                    .padding(start = 12.dp, end = 8.dp)
                    .size(22.dp)
            )

            Box(
                contentAlignment = Alignment.CenterStart,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                BasicTextField(
                    value = input,
                    keyboardOptions =
                        KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Search
                        ),
                    onValueChange = inputChanged,
                    textStyle = MaterialTheme.typography.bodyMedium.copy(fontSize = 15.sp),
                    modifier = Modifier.fillMaxWidth()
                )

                if (input.isEmpty()) {
                    Text(
                        label,
                        modifier = Modifier
                            .padding(start = 4.dp)
                            .alpha(0.6f),
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 15.sp),
                    )
                }
            }

            if (!input.isEmpty()) {
                Icon(
                    Icons.Default.Cancel, null,
                    modifier = Modifier
                        .padding(horizontal = 12.dp)
                        .size(16.dp)
                        .alpha(0.6f)
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onTap = { onClear() }
                            )
                        })
            }

        }
    }
}