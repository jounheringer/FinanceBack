package com.example.financeback.screens.compose

import android.content.Context
import android.icu.text.DateFormat
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.example.financeback.Globals
import com.example.financeback.classes.Category
import com.example.financeback.classes.Income
import com.example.financeback.classes.IncomeInfo
import com.example.financeback.controllers.IncomeController
import com.example.financeback.screens.Screen
import com.example.financeback.utils.CurrencyMask
import com.example.financeback.utils.NumberFormatter
import com.example.financeback.utils.PastOrPresentSelectableDates
import java.text.SimpleDateFormat
import java.util.Locale

class EditCompose (context: Context) {
    private val editContext = context
    private val category = Category()
    private val income = IncomeController(context)
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun EditScreen(
        modifier: Modifier = Modifier,
        incomeInfo: IncomeInfo,
        navController: NavController
    ) {
        val categories = category.getCategoriesByUser(editContext, Globals.getUser())
        val datePickerState =
            rememberDatePickerState(selectableDates = PastOrPresentSelectableDates)
        val focusManager = LocalFocusManager.current
        val dateFormatter = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale("pt-br"))

        var editIncome by remember { mutableStateOf(incomeInfo) }
        var selectedCategory by remember { mutableStateOf(categories[categories.indexOfFirst { it.id == incomeInfo.categoryID }]) }
        var expands by remember { mutableStateOf(false) }
        var missingParams by remember { mutableStateOf(false) }
        var showDatePicker by remember { mutableStateOf(false) }
        var number by remember {
            mutableStateOf(
                String.format("%.2f", editIncome.value).replace(".", "").replace(",", "")
            )
        }
        var incomeEdited by remember { mutableStateOf(false) }

        Column(modifier = modifier.fillMaxSize()) {
            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(15.dp)
            ) {
                IconButton(onClick = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                    }
                }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null
                    )
                }
            }

            Spacer(modifier = modifier.height(40.dp))
            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .height(500.dp),
                verticalArrangement = Arrangement.SpaceAround,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (showDatePicker) {
                    DatePickerDialog(onDismissRequest = { showDatePicker = false },
                        confirmButton = {
                            Button(onClick = {
                                datePickerState.selectedDateMillis?.let { millis ->
                                    editIncome.date = millis.plus(14400000)
                                }
                                showDatePicker = false
                            }) {
                                Text(text = "Selecionar")
                            }
                        }) {
                        DatePicker(state = datePickerState)
                    }
                }

                TextField(
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                    value = editIncome.name,
                    onValueChange = { data -> editIncome = editIncome.copy(name = data) },
                    placeholder = { Text(text = "Item") },
                    label = { Text(text = "Produto") }
                )

                TextField(
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    value = number,
                    onValueChange = {
                        number = if (it.startsWith("0")) ""
                        else it
                    },
                    visualTransformation = CurrencyMask(),
                    label = { Text(text = "Valor") }
                )

                TextField(
                    value = dateFormatter.format(editIncome.date),
                    onValueChange = { },
                    modifier = modifier.onFocusEvent {
                        if (it.isFocused) {
                            showDatePicker = true
                            focusManager.clearFocus(force = true)
                        }
                    },
                    label = { Text("Date") },
                    readOnly = true
                )

                ExposedDropdownMenuBox(
                    expanded = expands,
                    onExpandedChange = { expands = !expands }) {
                    TextField(modifier = modifier.menuAnchor(),
                        value = selectedCategory.name,
                        onValueChange = { },
                        readOnly = true,
                        label = { Text(text = "Categoria") },
                        trailingIcon = {
                            Icon(
                                imageVector = if (expands) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                                contentDescription = null
                            )
                        })

                    ExposedDropdownMenu(
                        expanded = expands,
                        onDismissRequest = { expands = false }) {
                        categories.forEach { category ->
                            DropdownMenuItem(
                                text = { Text(text = category.name) },
                                onClick = { selectedCategory = category; expands = false })
                        }
                    }
                }

                TextField(
                    value = editIncome.description,
                    onValueChange = { data -> editIncome = editIncome.copy(description = data) },
                    label = { Text(text = "Descrição") },
                    modifier = modifier.height(100.dp).fillMaxWidth(0.7f),
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
                )

                Button(onClick = {
                    editIncome.value = NumberFormatter().doubleFormatter(number)
                    missingParams = editIncome.missingParam().isNotEmpty()
                    if (!missingParams)
                        incomeEdited = income.editIncome(editIncome)
                }) {
                    Text(text = "Editar")
                }
            }

            if (missingParams) {
                AlertDialog(onDismissRequest = { missingParams = false },
                    title = { Text(text = "Campos faltando") },
                    text = {
                        Column() {
                            editIncome.missingParam().forEach { param ->
                                Text(text = "* $param")
                            }
                        }
                    },
                    confirmButton = {
                        Button(onClick = { missingParams = false }) {
                            Text(text = "Ok")
                        }
                    }
                )
            }

            if (incomeEdited) {
                AlertDialog(onDismissRequest = { incomeEdited = false },
                    text = { Text(text = "Nota alterada com sucesso!") },
                    confirmButton = {
                        Button(onClick = {
                            incomeEdited = false
                            navController.navigate(Screen.Home.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                            }
                        }) {
                            Text(text = "Ok")
                        }
                    }
                )
            }
        }
    }
}