package com.example.financeback.screens.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.financeback.classes.Accessibility

class ConfigurationsCompose () {
    @Composable
    fun ConfigurationsScreen(modifier: Modifier = Modifier) {
        val accessibility by remember { mutableStateOf(Accessibility()) }

        Column(modifier = modifier.fillMaxSize()) {
            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .heightIn(300.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceAround
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Modo escuro")
                    Spacer(modifier = modifier.width(10.dp))
                    Switch(
                        checked = accessibility.darkMode,
                        onCheckedChange = { accessibility.darkMode = !accessibility.darkMode })
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Tamanho da fonte")
                    Spacer(modifier = modifier.width(10.dp))
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        IconButton(
                            onClick = { accessibility.fontSize++ },
                            enabled = accessibility.fontSize < 32
                        ) {
                            Icon(
                                imageVector = Icons.Filled.KeyboardArrowUp,
                                contentDescription = null
                            )
                        }
                        OutlinedCard(modifier = modifier.width(IntrinsicSize.Max)) {
                            Column(
                                modifier = modifier
                                    .widthIn(48.dp, 128.dp)
                                    .height(48.dp),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = accessibility.fontSize.toString(),
                                    fontSize = 18.sp,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                        IconButton(
                            onClick = { accessibility.fontSize-- },
                            enabled = accessibility.fontSize > 10
                        ) {
                            Icon(
                                imageVector = Icons.Filled.KeyboardArrowDown,
                                contentDescription = null
                            )
                        }
                    }
                }
            }
        }
    }
}