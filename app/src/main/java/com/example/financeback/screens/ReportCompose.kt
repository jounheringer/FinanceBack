package com.example.financeback.screens

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.AlertDialog
import androidx.compose.material.RadioButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.financeback.classes.Income
import com.example.financeback.ui.theme.Negative
import com.example.financeback.ui.theme.Positive
import com.example.financeback.ui.theme.light_Positive
import com.example.financeback.utils.CustomDatePicker
import com.example.financeback.utils.NumberFormatter
import com.example.financeback.utils.bounceClick
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun ReportScreen(modifier: Modifier = Modifier, navigateToEdit: () -> Unit, context: Context) {
    val calendar = Calendar.getInstance()
    val numFormatter = NumberFormatter()
    var month by remember { mutableIntStateOf(calendar.get(Calendar.MONTH)) }
    val formatterMonth = numFormatter.decimalFormatter((month+1).toString())

    var year by remember { mutableIntStateOf(calendar.get(Calendar.YEAR)) }
    var date by remember { mutableStateOf("${year}-${formatterMonth}") }

    Column(modifier = modifier
        .fillMaxSize()
        .padding(8.dp, 12.dp)) {
        DateReportSelect(modifier = modifier,
            date = { date = it },
            monthChange = { month = it },
            month = month,
            yearChange = { year = it },
            year = year,
            tempDate = date)

        IncomeStatus(modifier = modifier, context, date)
        Spacer(modifier.height(10.dp))
        ShowAllIncomes(modifier, navigateTo = navigateToEdit, context = context, dateStamp = date)
    }
}

@Composable
fun DateReportSelect(modifier: Modifier,
                     date: (String) -> Unit,
                     monthChange: (Int) -> Unit,
                     month: Int,
                     yearChange: (Int) -> Unit,
                     year: Int,
                     tempDate: String) {
    var showDatePicker by rememberSaveable { mutableStateOf(false)}
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

    if(showDatePicker){
        CustomDatePicker(currentMonth = month,
                currentYear = year,
            currentMonthChange = { newMonth ->
                                monthChange(newMonth) },
            currentYearChange = { newYear ->
                                yearChange(newYear) },
            onConfirmButton = { monthChild, yearChild ->
                date("${yearChild}-${monthChild}")
                showDatePicker = false
            }
        ) {
            showDatePicker = false
        }
    }

    Row(modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically) {
        TextButton(onClick = { showDatePicker = true }) {
            Text(text = "${months[month]}/${year}")
        }
    }
}

@Composable
fun ShowAllIncomes(modifier: Modifier, navigateTo: () -> Unit, context: Context, dateStamp: String, limit: Int = 10) {
    val income = Income(null)
    val state = rememberScrollState()
    val filterOption = listOf("Total", "Positivo", "Negativo")
    val orderBy by remember { mutableStateOf("DESC") }

    var offset by remember { mutableIntStateOf(0) }
    var options by remember { mutableStateOf(false) }
    var incomeToProcess by remember { mutableIntStateOf(0) }
    var filter by remember { mutableStateOf(filterOption[0]) }

    val incomesCount = income.getIncomesCount(context = context, idUser = null, filter = filter)
    val incomes = income.getIncomes(context = context,
        limit = limit,
        offset = offset,
        filter = filter,
        timeStamp = dateStamp,
        orderByFlow = orderBy)

    Column(modifier = modifier
        .padding(0.dp, 6.dp)
        .verticalScroll(state)
    ) {
        Row(modifier = modifier
            .fillMaxWidth()
            .padding(0.dp, 0.dp, 10.dp, 0.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween) {
            filterOption.forEach { option ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selected = (filter == option), onClick = { filter = option })   
                    Text(text = option)
                }
            }
        }
        incomes?.forEach { income ->
            Card(
                modifier = modifier
                    .fillMaxWidth()
            ) {
                Row(modifier = modifier
                    .fillMaxWidth()) {
                    Column {
                        Text(
                            modifier = modifier.padding(8.dp, 4.dp),
                            text = "Nota nº ${income["ID"]}"
                        )
                        Text(
                            modifier = modifier.padding(8.dp, 4.dp),
                            text = "Item: ${income["Name"]}"
                        )
                        Text(
                            modifier = modifier.padding(8.dp, 4.dp),
                            text = "Preço: ${income["Value"]}",
                            color = if(income["Profit"] as Boolean) light_Positive else Negative
                        )
                        Text(
                            modifier = modifier.padding(8.dp, 4.dp),
                            text = "Data: ${SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(
                                income["Date"]
                            )}"
                        )
                        Text(
                            modifier = modifier.padding(8.dp, 4.dp),
                            text = "Descrição: ${income.getOrDefault("Description", "")}"
                        )
                    }
                    Spacer(modifier = modifier.weight(1f))
                    IconButton(onClick = { options = true
                        incomeToProcess = income["ID"] as Int
                    }) {
                        Icon(imageVector = Icons.Filled.MoreVert,
                            contentDescription = "Deletar")
                    }
                }
            }
            Spacer(modifier = modifier.height(10.dp))
        }

        if (options) {
            OptionsIncomeAlert(id = incomeToProcess,
                navigateToEdit = navigateTo,
                context = context,
                dismiss = { options = false })
        }

        if (incomesCount > 10){
            Row {
                if(offset >= 10) {
                    IconButton(modifier = modifier.bounceClick(),
                        onClick = { offset -= 10 }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = "Anterior"
                        )
                    }
                }
                Spacer(modifier = modifier.weight(1f))


                if (incomes?.count() == 10) {
                    IconButton(onClick = { offset += 10 }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = "Proximo"
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun OptionsIncomeAlert(modifier: Modifier = Modifier, id: Int, navigateToEdit: () -> Unit, context: Context, dismiss: () -> Unit ) {
    val income = Income(null)
    var delete by remember { mutableStateOf(false) }
    AlertDialog( modifier = modifier.size(250.dp, 150.dp),
        onDismissRequest = dismiss,
        text = { Text(text = "O que deseja fazer com a nota Nº${id}") },
        buttons = {
            Row(modifier = modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center) {
                Button(onClick = navigateToEdit) {
                    Text(text = "Editar")
                }
                Spacer(modifier = modifier.width(10.dp))
                Button(onClick = {delete = true}) {
                    Text(text = "Deletar")
                }
            }
        }
    )

    if (delete) {
        AlertDialog( modifier = modifier.size(250.dp, 150.dp),
            onDismissRequest = dismiss,
            text = { Text(text = "Deseja realmente deletar a nota fiscal Nº ${id}?") },
            buttons = {
                Row(modifier = modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center) {
                    Button(onClick = { income.deleteIncome(context, null, id)
                                    dismiss()}) {
                        Text(text = "Deletar")
                    }
                    Spacer(modifier = modifier.width(10.dp))
                    Button(onClick = dismiss) {
                        Text(text = "Cancelar")
                    }
                }
            })
    }
}


@Composable
fun IncomeStatus(modifier: Modifier, context: Context, timeStamp: String) {
    val income = Income()
    val incomeStatus = income.getIncomeTotals(context, timeStamp)
    val state = rememberScrollState()

    Column(modifier = modifier.padding(0.dp, 12.dp)) {
        Row(
            modifier = modifier
                .horizontalScroll(state),
            verticalAlignment = Alignment.CenterVertically
        ) {
            incomeStatus.forEach { status ->
                Column(
                    modifier = modifier
                        .background(
                            when (status.key) {
                                "Total" -> MaterialTheme.colorScheme.surfaceContainer
                                "Positivo" -> Positive
                                "Negativo" -> Negative
                                else -> MaterialTheme.colorScheme.background
                            },
                            shape = RoundedCornerShape(10.dp)
                        )
                        .size(200.dp, 100.dp)
                        .bounceClick(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "R$: ${status.value}")
                    Text(text = "Valor ${status.key}")
                }
                if(status.key != "Negativo")
                    Spacer(modifier = modifier.width(10.dp))
            }
        }
    }
}