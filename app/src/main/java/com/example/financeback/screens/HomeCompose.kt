package com.example.financeback.screens

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.financeback.classes.Income
import com.example.financeback.ui.theme.Negative
import com.example.financeback.ui.theme.Positive
import com.example.financeback.ui.theme.light_Positive
import com.example.financeback.utils.bounceClick
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun HomeScreen(modifier: Modifier = Modifier, context: Context = LocalContext.current, navigateTo: () -> Unit) {
    Column(
        modifier
            .fillMaxHeight()
            .fillMaxWidth()
            .padding(8.dp),
        verticalArrangement = Arrangement.SpaceBetween) {
        IncomeStatus(modifier, context, "2024-04")
        RecentIncomes(context = context, navigateTo = navigateTo)
    }
}

@Composable
fun RecentIncomes(modifier: Modifier = Modifier, context: Context, navigateTo: () -> Unit) {
    val state = rememberScrollState()
    val income = Income()
    val recentIncomes = income.getIncomes(context, limit = 5, offset = 0, filter = "Total", timeStamp = "2024-04")

    var options by remember { mutableStateOf(false) }
    var incomeToProcess by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) { state.animateScrollTo(100)}

    Column(
        modifier
            .verticalScroll(rememberScrollState())
            .height(400.dp)) {
        Row(modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center){
            Text(text = "Notas recentes",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold)
        }
        recentIncomes.forEach { income ->
            var expand by remember { mutableStateOf(false)}
            Spacer(modifier = modifier.height(10.dp))
            Column (
                modifier
                    .fillMaxWidth()
                    .background(
                        color = MaterialTheme.colorScheme.onError,
                        shape = RoundedCornerShape(10.dp)
                    )) {
                Row(verticalAlignment = Alignment.CenterVertically,
                    modifier = modifier
                        .padding(10.dp, 5.dp)
                        .height(30.dp)
                        .clickable {
                            expand = !expand
                        }) {
                    Row() {
                        Text(text = "${income["Name"]}: ")
                        Text(text = "R$${income["Value"]}",
                            color = if(income["Profit"] as Boolean) light_Positive else Negative)
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    IconButton(onClick = { options = true
                        incomeToProcess = income["ID"] as Int}) {
                        Icon(imageVector = Icons.Filled.MoreVert,
                            contentDescription = "Opções")
                    }
                    Icon(imageVector = if (!expand) Icons.Filled.KeyboardArrowDown else Icons.Filled.KeyboardArrowUp,
                        contentDescription = "Mais informações")
                }
                if(expand){
                    Column(modifier = modifier.padding(10.dp, 5.dp)) {
                        Text(text = "Nota Nº ${income["ID"]}")
                        Text(text = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(income.get("Date")))
                        Text(text = "Descrição: ${income["Description"]}")
                    }
                }

                if (options){
                    OptionsIncomeAlert(id = incomeToProcess,
                        navigateToEdit = navigateTo,
                        context = context,
                        dismiss = { options = false })
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