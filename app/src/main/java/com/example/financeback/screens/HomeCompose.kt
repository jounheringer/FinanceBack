package com.example.financeback.screens

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.financeback.classes.Income
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun HomeScreen(modifier: Modifier = Modifier, context: Context = LocalContext.current, navigateTo: () -> Unit) {
    Column(modifier.fillMaxHeight(),
        verticalArrangement = Arrangement.SpaceBetween) {
        IncomeStatus(modifier, context, "2024-04")
        RecentIncomes(context = context)
    }
}

@Composable
fun RecentIncomes(modifier: Modifier = Modifier, context: Context) {
    val state = rememberScrollState()
    val income = Income()
    val recentIncomes = income.getIncomes(context, limit = 5, offset = 0, filter = "Total", timeStamp = "2024-04")
    LaunchedEffect(Unit) { state.animateScrollTo(100)}

    Column(
        modifier
            .padding(15.dp)
            .verticalScroll(rememberScrollState())
            .height(400.dp)) {
        recentIncomes?.forEach {income ->
            var expand by remember { mutableStateOf(false)}
            var delete by remember { mutableStateOf(false) }
            var edit by remember { mutableStateOf(false) }
            Spacer(modifier = modifier.height(10.dp))
            Column (
                modifier
                    .fillMaxWidth()
                    .padding(10.dp)
                    .background(
                        color = MaterialTheme.colorScheme.onError,
                        shape = RoundedCornerShape(10.dp)
                    )) {
                Row(verticalAlignment = Alignment.CenterVertically,
                    modifier = modifier
                        .padding(5.dp)
                        .height(30.dp)
                        .clickable {
                            expand = !expand
                        }) {
                    Text(text = "Nº${income.get("ID")} ${income.get("Name")}: R$${income.get("Value")}")
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(imageVector = Icons.Filled.Delete,
                        contentDescription = "Deletar",
                        modifier = modifier.clickable { delete = true })
                    Icon(imageVector = Icons.Filled.Create,
                        contentDescription = "Editar",
                        modifier = modifier.clickable { edit = true })
                    Icon(imageVector = if (!expand) Icons.Filled.KeyboardArrowDown else Icons.Filled.KeyboardArrowUp,
                        contentDescription = "Mais informações")

                }
                if(expand){
                    Column(modifier.padding(5.dp)) {
                        Text(text = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(income.get("Date")))
                        Text(text = "Descricao: ")
                    }
                }

                if (edit){
                    AlertDialog(onDismissRequest = { edit = false },
                        confirmButton = { Button(onClick = { edit = false }) {
                            Text("Editar")
                        }},
                        dismissButton = {Button(onClick = { edit = false }) {
                            Text("Nao Editar")
                        }},
                        text = { Text(text = "Editar nota Nº${income.get("ID")}") })
                }
                if (delete){
                    AlertDialog(onDismissRequest = { delete = false },
                        confirmButton = { Button(onClick = { delete = false }) {
                            Text("Deletar")
                        } },
                        dismissButton = {Button(onClick = { delete = false }) {
                            Text("Nao Deletar")
                        } },
                        text = { Text(text = "Deletar nota Nº${income.get("ID")}") })
                }
            }
        }
    }
}

//@Preview
//@Composable
//fun HomeScreenPreview() {
//    HomeScreen()
//}