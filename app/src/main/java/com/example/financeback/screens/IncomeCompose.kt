package com.example.financeback.screens

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextField
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.financeback.classes.Income
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IncomeScreen(modifier:Modifier = Modifier, context: Context) {


    Column(modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly) {
        Column(modifier = modifier.width(300.dp),
            horizontalAlignment = Alignment.CenterHorizontally) {
            IncomeInputs(context = context)
        }


    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IncomeInputs(modifier: Modifier = Modifier
    .padding(0.dp, 8.dp)
    .fillMaxWidth(), context: Context){
    val incomeOptions = listOf("Positivo", "Negativo")
    val focusManager = LocalFocusManager.current

    var text by remember { mutableStateOf("") }
    var number by remember { mutableStateOf("") }
    var dateStamp by remember { mutableStateOf(System.currentTimeMillis().toBrazilianDateFormat())}
    var description by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }
    var datePickerState = rememberDatePickerState()
    var optionSelected by remember { mutableStateOf(incomeOptions[0]) }

    if (showDatePicker) {
        DatePickerDialog(onDismissRequest = { showDatePicker = false },
            confirmButton = {
                Button(onClick = { datePickerState.selectedDateMillis?.let {millis ->
                    dateStamp = millis.toBrazilianDateFormat()}
                    showDatePicker = false}) {
                    Text(text = "Selecionar")
                }
            }) {
            DatePicker(state = datePickerState)
        }
    }

    TextField(value = text,
        onValueChange = { text = it },
        placeholder = { Text(text = "Item") },
        label = { Text(text = "Produto")},
        modifier = modifier)

    TextField(keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        value = number,
        onValueChange = { number = it },
        placeholder = { Text(text = "0.0") },
        label = { Text(text = "Valor")},
        modifier = modifier)

    TextField(
        value = dateStamp,
        onValueChange = { },
        modifier = modifier.onFocusEvent {
                if (it.isFocused) {
                    showDatePicker = true
                    focusManager.clearFocus(force = true)
                }
            },
        label = {
            Text("Date")
        },
        readOnly = true
    )
    Row() {
        incomeOptions.forEach { option ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(selected = option == optionSelected, onClick = { optionSelected = option })
                Text(text = option)
            }
        }
    }

    TextField(value = description,
        onValueChange = { description = it },
        label = { Text(text = "Descrição") },
        modifier = modifier
            .height(100.dp)
            .height(48.dp))

    Column(modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally) {
        SaveIncome(context = context,
            text = text,
            number = number,
            dateStamp = dateStamp,
            optionSelected = optionSelected,
            description = description)
    }
}

@Composable
fun SaveIncome(modifier:Modifier = Modifier,
               context:Context, text:String,
               number:String, dateStamp:String,
               optionSelected:String,
               description:String){
    val income = Income()

    var missingParams by remember { mutableStateOf(false) }
    var canSaveIncome by remember { mutableStateOf<Long?>(null) }
    var missingValues = mutableListOf<String>()

    if (text.isEmpty()){ missingValues.add("Produto") }
    if (number.isEmpty()){ missingValues.add("Valor") }
    if (dateStamp.isEmpty()){ missingValues.add("Produto") }
    if (optionSelected.isEmpty()){ missingValues.add("Valor") }

    Button(onClick = { if(missingValues.isNotEmpty())
        missingParams = true
    else
        canSaveIncome = income.saveIncome(context = context,
        value = number.toFloat(),
        name = text,
        dateStamp = dateStamp,
        profit = if (optionSelected == "Positivo") true else false,
        description =  description) }) {
        Text(text = "Salvar")
    }

    if (missingParams) {
        AlertDialog(
            title = {
                Text(text = "Campos faltando")
            },
            text = {
                Column {
                    missingValues.forEach { value ->
                        Text(text = "*${value}")}
                }
            },
            onDismissRequest = {
                        missingParams = false},
            confirmButton = {
                TextButton(
                    onClick = {
                        missingParams = false
                    }
                ) {
                    Text("OK")
                }
            }
        )
    }

    if (canSaveIncome != null) {
        AlertDialog(
            title = {
                Text(text = "Nota fiscal salva")
            },
            text = {
                Text(text = "Nota fiscal salva Nº${canSaveIncome} com sucesso")
            },
            onDismissRequest = {
                canSaveIncome = null},
            confirmButton = {
                TextButton(
                    onClick = {
                        canSaveIncome = null
                    }
                ) {
                    Text("OK")
                }
            }
        )
    }
}

fun Long.toBrazilianDateFormat(
    pattern: String = "dd/MM/yyyy"
): String {
    val date = Date(this)
    val formatter = SimpleDateFormat(
        pattern, Locale("pt-br")
    ).apply {
        timeZone = TimeZone.getTimeZone("GMT")
    }
    return formatter.format(date)
}

@Preview(showBackground = true)
@Composable
fun IncomeScreenPreview() {
    IncomeScreen(context = LocalContext.current)
}