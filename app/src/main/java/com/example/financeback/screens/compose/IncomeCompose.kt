package com.example.financeback.screens.compose

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.financeback.classes.Category
import com.example.financeback.classes.CategoryInfo
import com.example.financeback.classes.Income
import com.example.financeback.classes.IncomeInfo
import com.example.financeback.utils.CurrencyMask
import com.example.financeback.utils.NumberFormatter
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun IncomeScreen(modifier:Modifier = Modifier, userID: Int) {
    val context = LocalContext.current
    Column(modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly) {
        Column(modifier = modifier.width(300.dp),
            horizontalAlignment = Alignment.CenterHorizontally) {
            IncomeInputs(context = context, userID = userID, modifier = modifier
                .padding(0.dp, 8.dp)
                .fillMaxWidth())
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IncomeInputs(modifier: Modifier, context: Context, userID: Int){
    val category = Category()
    val categories = category.getCategoriesByUser(context, userID)
    val focusManager = LocalFocusManager.current
    val datePickerState = rememberDatePickerState()

    var selectedCategory by remember { mutableStateOf(categories[0]) }
    var expands by remember { mutableStateOf(false) }
    var incomeInfo by remember { mutableStateOf(IncomeInfo()) }
    var number by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }
    var incomeSaved by remember { mutableStateOf(false) }
    var newCategory by remember { mutableStateOf(false) }
    var newCategoryName by remember { mutableStateOf("") }
    var newCategoryProfit by remember { mutableStateOf(false) }

    if (incomeSaved) {
        incomeInfo = IncomeInfo()
        number = ""
    }

    if (newCategory) {
        AlertDialog(onDismissRequest = { newCategory = false },
            title = { Text(text = "Nova categoria") },
            text = { Column {
                OutlinedTextField(value = newCategoryName, onValueChange = { newCategoryName = it }, label = { Text(
                    text = "Nome"
                ) })
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = newCategoryProfit, onCheckedChange = { newCategoryProfit = true } )
                    Text(text = "Positivo")
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = !newCategoryProfit, onCheckedChange = { newCategoryProfit = false } )
                    Text(text = "Negativo")
                }
            }},
            confirmButton = { Button(onClick = { category.saveCategoryByUser(context, userID, CategoryInfo(newCategoryName, newCategoryProfit))
                newCategoryName = ""
                newCategory = false },
                enabled = newCategoryName.isNotEmpty()) {
                Text(text = "Salvar")
            } })
    }

    if (showDatePicker) {
        DatePickerDialog(onDismissRequest = { showDatePicker = false },
            confirmButton = {
                Button(onClick = { datePickerState.selectedDateMillis?.let {millis ->
                    incomeInfo.date = millis}
                    showDatePicker = false}) {
                    Text(text = "Selecionar")
                }
            }) {
            DatePicker(state = datePickerState)
        }
    }

    TextField(keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
        value = incomeInfo.name,
        onValueChange = { data -> incomeInfo = incomeInfo.copy(name = data) },
        placeholder = { Text(text = "Item") },
        label = { Text(text = "Produto")},
        modifier = modifier)

    TextField(keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        value = number,
        onValueChange = {number = if(it.startsWith("0")) ""
            else it
        },
        visualTransformation = CurrencyMask(),
        label = { Text(text = "Valor")},
        modifier = modifier)

    TextField(
        value = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(incomeInfo.date),
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


    ExposedDropdownMenuBox(expanded = expands, onExpandedChange = { expands = !expands }) {
        TextField(modifier = modifier
            .fillMaxWidth()
            .menuAnchor(),
            value = selectedCategory.name,
            onValueChange = {  },
            readOnly = true,
            label = { Text(text = "Categoria")},
            trailingIcon = { Icon(imageVector = if (expands) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown, contentDescription = null) })

        ExposedDropdownMenu(expanded = expands, onDismissRequest = { expands = false }) {
            categories.forEach { category ->
                DropdownMenuItem(text = { Text(text = category.name) }, onClick = { selectedCategory = category; expands = false })
            }
            DropdownMenuItem(text = { Text(text = "Adicionar nova categoria") }, onClick = { newCategory = true })
        }
    }

    TextField(value = incomeInfo.description,
        onValueChange = { data -> incomeInfo = incomeInfo.copy(description = data) },
        label = { Text(text = "Descrição") },
        modifier = modifier
            .height(100.dp),
        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences))

    Column(modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally) {
        SaveIncome(context, incomeInfo, number) { incomeSaved = it }
    }
}

@Composable
fun SaveIncome(context: Context, incomeInfo: IncomeInfo, number: String, incomeSaved: (Boolean) -> Unit){
    val income = Income()
    var missingParams by remember { mutableStateOf(false) }
    var saveIncomeResult by remember { mutableStateOf(false) }
    var missingValues by remember { mutableStateOf<Array<String>>(arrayOf()) }

    Button(colors = ButtonColors(MaterialTheme.colorScheme.onPrimary,
        MaterialTheme.colorScheme.onBackground,
        MaterialTheme.colorScheme.errorContainer,
        MaterialTheme.colorScheme.error),onClick = {
        if (number.isNotEmpty())
            incomeInfo.value = NumberFormatter().doubleFormatter(number)
        missingValues = incomeInfo.missingParam()
        if(missingValues.isNotEmpty())
            missingParams = true
        else
            saveIncomeResult = income.saveIncome(context = context, incomeInfo = incomeInfo) }) {
        Text(text = "Salvar")
    }

    if (missingParams) {
        AlertDialog(
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
                    onClick = { missingParams = false }) {
                    Text("OK")
                }
            }
        )
    }

    if (saveIncomeResult) {
        AlertDialog(
            text = {
                Text(text = "Nota fiscal salva com sucesso")
            },
            onDismissRequest = {
                saveIncomeResult = false},
            confirmButton = {
                TextButton(
                    onClick = { saveIncomeResult = false ; incomeSaved(true) }) {
                    Text("OK")
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun IncomeScreenPreview() {
    IncomeScreen(userID = 1)
}