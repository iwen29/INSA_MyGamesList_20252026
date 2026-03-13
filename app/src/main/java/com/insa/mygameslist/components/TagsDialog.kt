package com.insa.mygameslist.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

@Composable
fun TagsDialog(
    onDismissRequest: () -> Unit,
    tags: Set<String>,
    setTags: (Set<String>) -> Unit,
) {
    var textInput by remember { mutableStateOf("") }
    var tempTags by remember(tags) { mutableStateOf(tags.toSet()) }

    Dialog(onDismissRequest = { onDismissRequest() }) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Filter by tags",
                    textAlign = TextAlign.Center,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                OutlinedTextField(
                    value = textInput,
                    onValueChange = { textInput = it },
                    placeholder = { Text("Start typing a tag...") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            if (textInput.isNotBlank()) {
                                tempTags = tempTags + textInput
                            }
                            textInput = ""
                        }
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                )
                if (tempTags.isNotEmpty()) {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        tempTags.forEach {
                            CompositionLocalProvider( // Sert à empêcher les trucs de rajouter du padding vertical pour rien
                                LocalMinimumInteractiveComponentSize provides 0.dp
                            ) {
                                InputChip(
                                    label = { Text(it) },
                                    onClick = { tempTags = tempTags - it },
                                    selected = false,
                                    trailingIcon = {
                                        Icon(
                                            Icons.Default.Close,
                                            contentDescription = "Remove"
                                        )
                                    },
                                    modifier = Modifier.padding(0.dp)
                                )
                            }
                        }
                    }
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(top = 16.dp)
                ) {
                    Button(
                        onClick = { onDismissRequest() },
                        colors = ButtonColors(
                            containerColor = Color.hsv(0f, 0f, .75f),
                            contentColor = Color.Black,
                            disabledContainerColor = Color.LightGray,
                            disabledContentColor = Color.Black
                        )
                    ) {
                        Text("Cancel")
                    }
                    Button(
                        onClick = {
                            if (textInput.isNotBlank()) { // L'utilisateur s'attendra probablement à ce que le tag soit ajouté même s'il ne l'a pas validé
                                tempTags = tempTags + textInput
                            }
                            textInput = ""
                            setTags(tempTags)
                            onDismissRequest()
                        },
                        colors = ButtonColors(
                            containerColor = Color.hsv(209f, 0.47f, 1f),
                            contentColor = Color.Black,
                            disabledContainerColor = Color.LightGray,
                            disabledContentColor = Color.Black
                        )
                    ) {
                        Text("OK")
                    }
                }
            }
        }
    }
}