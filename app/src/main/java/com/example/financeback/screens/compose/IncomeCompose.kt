package com.example.financeback.screens.compose

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.material.icons.filled.Delete
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
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.financeback.Globals
import com.example.financeback.classes.Category
import com.example.financeback.classes.CategoryInfo
import com.example.financeback.classes.IncomeInfo
import com.example.financeback.controllers.IncomeController
import com.example.financeback.utils.CurrencyMask
import com.example.financeback.utils.NumberFormatter
import com.example.financeback.utils.PastOrPresentSelectableDates
import java.text.SimpleDateFormat
import java.util.Locale

class IncomeCompose (context: Context) {
    private val incomeContext = context
    private val category = Category(incomeContext)
    private val income = IncomeController(context)
    @Composable
    fun IncomeScreen(modifier: Modifier = Modifier) {
        Column(
            modifier = modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Column(
                modifier = modifier.width(300.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                IncomeInputs(modifier = modifier
                    .padding(0.dp, 8.dp)
                    .fillMaxWidth()
                )
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
    @Composable
    fun IncomeInputs(modifier: Modifier) {
        val focusManager = LocalFocusManager.current
        val datePickerState = rememberDatePickerState(selectableDates = PastOrPresentSelectableDates)

        var selectedCategory by remember { mutableStateOf(CategoryInfo()) }
        var incomeInfo by remember { mutableStateOf(IncomeInfo()) }
        var number by remember { mutableStateOf("") }
        var showDatePicker by remember { mutableStateOf(false) }
        var incomeSaved by remember { mutableStateOf(false) }

        if (incomeSaved) {
            incomeInfo = IncomeInfo()
            number = ""
            incomeSaved = false
        }

        if (showDatePicker) {
            DatePickerDialog(onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    Button(onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            incomeInfo.date = millis.plus(14400000)
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
            value = incomeInfo.name,
            onValueChange = { data -> incomeInfo = incomeInfo.copy(name = data) },
            placeholder = { Text(text = "Item") },
            label = { Text(text = "Produto") },
            modifier = modifier
        )

        TextField(
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            value = number,
            onValueChange = {
                number = if (it.startsWith("0")) ""
                else it
            },
            visualTransformation = CurrencyMask(),
            label = { Text(text = "Valor") },
            modifier = modifier
        )

        TextField(
            value = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(
                incomeInfo.date.plus(
                    86400
                )
            ),
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

        CategoriesList(modifier = modifier, selectedCategory = selectedCategory) {selectedCategory = it}

        TextField(
            value = incomeInfo.description,
            onValueChange = { data -> incomeInfo = incomeInfo.copy(description = data) },
            label = { Text(text = "Descrição") },
            modifier = modifier
                .height(100.dp),
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
        )

        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SaveIncome(incomeInfo, number, selectedCategory) { incomeSaved = it }
        }
    }

    @Composable
    fun SaveIncome(
        incomeInfo: IncomeInfo,
        number: String,
        categoryInfo: CategoryInfo,
        incomeSaved: (Boolean) -> Unit
    ) {
        var missingParams by remember { mutableStateOf(false) }
        var saveIncomeResult by remember { mutableStateOf(false) }

        Button(colors = ButtonColors(
            MaterialTheme.colorScheme.onPrimary,
            MaterialTheme.colorScheme.onBackground,
            MaterialTheme.colorScheme.errorContainer,
            MaterialTheme.colorScheme.error
        ), onClick = {
            incomeInfo.value = NumberFormatter().doubleFormatter(number)
            incomeInfo.date -= 14400000
            missingParams = incomeInfo.missingParam().isNotEmpty()
            if (!missingParams)
                saveIncomeResult = income.saveIncome(incomeInfo, categoryInfo)
        }) {
            Text(text = "Salvar")
        }

        if (missingParams) {
            AlertDialog(
                text = {
                    Column {
                        incomeInfo.missingParam().forEach { value ->
                            Text(text = "*${value}")
                        }
                    }
                },
                onDismissRequest = {
                    missingParams = false
                },
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
                    saveIncomeResult = false
                },
                confirmButton = {
                    TextButton(
                        onClick = { saveIncomeResult = false; incomeSaved(true) }) {
                        Text("OK")
                    }
                }
            )
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun CategoriesList(modifier: Modifier, enableAddIncome: Boolean = true, selectedCategory: CategoryInfo, onChangeSelectedCategory:(CategoryInfo) -> Unit) {
        val categories = category.getCategoriesByUser(Globals.getUser(), false)
        var expands by remember { mutableStateOf(false) }
        var categoryOption by remember { mutableStateOf(CategoryInfo()) }
        var newCategory by remember { mutableStateOf(false) }
        var newCategoryName by remember { mutableStateOf("") }
        var newCategoryProfit by remember { mutableStateOf(false) }

        if (selectedCategory.name.isEmpty())
            onChangeSelectedCategory(categories[0])

        if (categoryOption.id >= 2) {
            AlertDialog(title = { Text(text = "Deletar categoria")},
                text = { Text(text = "Deseja deletar a categoria ${categoryOption.name}?") },
                onDismissRequest = { categoryOption = CategoryInfo() },
                confirmButton = { Button(onClick = {
                    if (category.deleteCategory(categoryOption.id))
                        categoryOption = CategoryInfo()
                    else
                        Toast.makeText(incomeContext, "Erro ao deletar a categoria, tente novamente", Toast.LENGTH_SHORT).show()}) {
                    Text(text = "Deletar")
                } })
        }

        if (newCategory) {
            AlertDialog(onDismissRequest = { newCategory = false },
                title = { Text(text = "Nova categoria") },
                text = {
                    Column {
                        OutlinedTextField(
                            value = newCategoryName,
                            onValueChange = { newCategoryName = it },
                            label = {
                                Text(
                                    text = "Nome"
                                )
                            })
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = newCategoryProfit,
                                onCheckedChange = { newCategoryProfit = true })
                            Text(text = "Positivo")
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = !newCategoryProfit,
                                onCheckedChange = { newCategoryProfit = false })
                            Text(text = "Negativo")
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            category.saveCategoryByUser(
                                Globals.getUser(),
                                CategoryInfo(0, newCategoryName, newCategoryProfit)
                            )
                            newCategoryName = ""
                            newCategory = false
                        },
                        enabled = newCategoryName.isNotEmpty()
                    ) {
                        Text(text = "Salvar")
                    }
                })
        }

        ExposedDropdownMenuBox(expanded = expands, onExpandedChange = { expands = !expands }) {
            TextField(modifier = modifier
                .fillMaxWidth()
                .menuAnchor(),
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

            ExposedDropdownMenu(expanded = expands, onDismissRequest = { expands = false }) {
                categories.forEach { category ->
                    DropdownMenuItem(text = { Text(text = category.name) },
                        onClick = { onChangeSelectedCategory(category)
                            expands = false},
                        trailingIcon = { if (category.userID != null)
                            IconButton(onClick = { categoryOption = category }) {
                                Icon(imageVector = Icons.Filled.Delete,
                                    contentDescription = "Deletar categoria")
                            }
                        }
                    )
                }
                if (enableAddIncome)
                    DropdownMenuItem(text = { Text(text = "Adicionar nova categoria") },
                        onClick = { newCategory = true })
            }
        }
    }
}