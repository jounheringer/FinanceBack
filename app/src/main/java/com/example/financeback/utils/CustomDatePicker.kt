package com.example.financeback.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun CustomDatePicker(
    modifier: Modifier = Modifier,
    visible: Boolean = true,
    currentMonthChange: (Int) -> Unit,
    currentMonth: Int,
    currentYearChange: (Int) -> Unit,
    currentYear: Int,
    onConfirmButton: (String, String) -> Unit,
    onCancelClicked: () -> Unit) {
    val months = listOf(
        "JAN",
        "FEV",
        "MAR",
        "ABR",
        "MAI",
        "JUN",
        "JUL",
        "AGO",
        "SET",
        "OUT",
        "NOV",
        "DEZ"
    )

    val rows = 3
    val interactionSource = remember {
        MutableInteractionSource()
    }

    var monthSelected by remember { mutableStateOf(months[currentMonth]) }
    var yearSelected by remember { mutableIntStateOf(currentYear) }
    var returnMonth: String

if(visible) {
    AlertDialog(modifier = modifier.size(250.dp, 350.dp),
        backgroundColor = MaterialTheme.colorScheme.background,
        shape = RoundedCornerShape(10),
        title = { Text(
            modifier = modifier.padding(0.dp, 10.dp),
            text = "Selecione Mês e Ano",
            color = MaterialTheme.colorScheme.onBackground
        ) },
        text = {
            Column(
                modifier = modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                Row(
                    modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(onClick = { yearSelected-- }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = "Anterior",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    Text(text = yearSelected.toString(), color = MaterialTheme.colorScheme.onBackground)
                    IconButton(onClick = { yearSelected++ }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = "Proximo",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }

                FlowRow(maxItemsInEachRow = rows) {
                    months.forEach { month ->
                        Box(modifier = modifier
                                .padding(4.dp)
                                .height(30.dp)
                                .weight(1f)
                                .clickable(
                                    indication = null,
                                    interactionSource = interactionSource,
                                    onClick = {
                                        monthSelected = month
                                    } )
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (monthSelected == month) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.background)
                                .border(width = 0.5.dp, color = MaterialTheme.colorScheme.onBackground, shape = RoundedCornerShape(15.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = month,
                                textAlign = TextAlign.Center,
                                color = if (monthSelected == month) MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }

                }
            }
        },
        buttons = {
            Button(modifier = modifier
                .fillMaxWidth()
                .padding(4.dp, 8.dp),
                onClick = {
                    returnMonth = NumberFormatter().decimalFormatter((months.indexOf(monthSelected) + 1).toString())
                    onConfirmButton(
                    returnMonth,
                    yearSelected.toString())
                    currentMonthChange(months.indexOf(monthSelected))
                    currentYearChange(yearSelected)
                })
            {
                Text(
                    text = "Selecionar",
                    fontSize = 10.sp
                )
            }
          },
            onDismissRequest = { onCancelClicked() }
        )
    }
}