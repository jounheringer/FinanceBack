package com.example.financeback.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    Column(modifier.fillMaxHeight()
        ,verticalArrangement = Arrangement.SpaceBetween) {
        Incomes()
        RecentIncomes()
    }
}

@Composable
fun Incomes(modifier: Modifier = Modifier) {
    val state = rememberScrollState()
    LaunchedEffect(Unit) { state.animateScrollTo(100)}
    
    Row(
        modifier = modifier
            .padding(15.dp)
            .horizontalScroll(rememberScrollState()),
        verticalAlignment = Alignment.CenterVertically){
        Column(
            modifier
                .background(Color.Gray, shape = RoundedCornerShape(10.dp))
                .size(200.dp, 100.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "10.4")
            Text(text = "Valor Total")
        }
        Spacer(modifier = modifier.width(15.dp))
        Column(
            modifier
                .background(Color.Green, shape = RoundedCornerShape(10.dp))
                .size(200.dp, 100.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "5.2")
            Text(text = "Total de lucros")
        }
        Spacer(modifier = modifier.width(15.dp))
        Column(
            modifier
                .background(Color.Red, shape = RoundedCornerShape(10.dp))
                .size(200.dp, 100.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "-5.2")
            Text(text = "Total de dispesa")
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RecentIncomes(modifier: Modifier = Modifier) {
    val incomes = listOf(
        "5.2", "-1.6", "59.00", "44.99", "-10.00"
    )
    val state = rememberScrollState()
    LaunchedEffect(Unit) { state.animateScrollTo(100)}

    Column(
        modifier
            .padding(15.dp)
            .verticalScroll(rememberScrollState())) {
        incomes.forEach {income ->
            var expand by remember { mutableStateOf(false)}
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
                    modifier = modifier.padding(5.dp)) {
                    Text(text = income)
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(imageVector = Icons.Filled.Delete, contentDescription = "Deletar")
                    Icon(imageVector = Icons.Filled.Create, contentDescription = "Editar")
                    Icon(imageVector = if (!expand) Icons.Filled.KeyboardArrowDown else Icons.Filled.KeyboardArrowUp,
                        contentDescription = "Mais informações",
                        modifier = modifier.clickable {
                                expand = !expand
                            }
                        )

                }
                if(expand){
                    Column(modifier.padding(5.dp)) {
                        Text(text = "Data: 07/01/2003")
                        Text(text = "Estabelecimento: Lojinha")
                        Text(text = "Descricao: ")
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun HomeScreenPreview() {
    HomeScreen()
}