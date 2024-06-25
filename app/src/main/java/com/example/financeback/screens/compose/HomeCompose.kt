package com.example.financeback.screens.compose

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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.example.financeback.classes.Income
import com.example.financeback.controllers.IncomeController
import com.example.financeback.screens.Screen
import com.example.financeback.ui.theme.ExtendedColorScheme
import com.example.financeback.ui.theme.negativeLight
import com.example.financeback.ui.theme.positiveLight
import com.example.financeback.utils.NumberFormatter
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class HomeCompose (context: Context) {
    private val homeContext = context
    private val income = IncomeController(context)
    @Composable
    fun HomeScreen(modifier: Modifier = Modifier, navController: NavController) {
        val calendar = Calendar.getInstance()
        val date = "${calendar.get(Calendar.YEAR)}-${
            NumberFormatter().decimalFormatter(
                (calendar.get(Calendar.MONTH) + 1).toString()
            )
        }"
        Column(
            modifier
                .fillMaxHeight()
                .fillMaxWidth()
                .padding(8.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            ReportCompose(homeContext).IncomeStatus(modifier, date)
            RecentIncomes(modifier, navController, date)
        }
    }

    @Composable
    fun RecentIncomes(
        modifier: Modifier = Modifier,
        navController: NavController,
        date: String
    ) {
        val state = rememberScrollState()
        val recentIncomes = income.getAllIncomes(limit = 5, timeStamp = date)

        var options by remember { mutableStateOf(false) }
        var incomeToProcess by remember { mutableIntStateOf(0) }
        LaunchedEffect(Unit) { state.animateScrollTo(100) }

        Column(
            modifier
                .height(400.dp)
        ) {
            Row(
                modifier = modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Notas recentes",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            if (recentIncomes.isNotEmpty()) {
                Column(modifier = modifier.verticalScroll(state)) {
                    recentIncomes.forEach { income ->
                        var expand by remember { mutableStateOf(false) }
                        Spacer(modifier = modifier.height(10.dp))
                        Column(
                            modifier
                                .fillMaxWidth()
                                .heightIn(0.dp, 300.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.background,
                                    shape = RoundedCornerShape(10.dp)
                                )
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically,
                                modifier = modifier
                                    .padding(10.dp, 5.dp)
                                    .height(30.dp)
                                    .clickable {
                                        expand = !expand
                                    }) {
                                Row {
                                    Text(
                                        text = "${income["Name"]}: ",
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                    Text(
                                        text = "R$${income["Value"]}",
                                        color = if (income["Profit"] as Boolean) positiveLight else negativeLight
                                    )
                                }
                                Spacer(modifier = Modifier.weight(1f))
                                IconButton(onClick = {
                                    options = true
                                    incomeToProcess = income["ID"] as Int
                                }) {
                                    Icon(
                                        imageVector = Icons.Filled.MoreVert,
                                        contentDescription = "Opções",
                                        tint = MaterialTheme.colorScheme.onBackground
                                    )
                                }
                                Icon(
                                    imageVector = if (!expand) Icons.Filled.KeyboardArrowDown else Icons.Filled.KeyboardArrowUp,
                                    contentDescription = "Mais informações",
                                    tint = MaterialTheme.colorScheme.onBackground
                                )
                            }
                            if (expand) {
                                Column(modifier = modifier.padding(10.dp, 5.dp)) {
                                    Text(
                                        text = "Nota Nº ${income["ID"]}",
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                    Text(
                                        text = SimpleDateFormat(
                                            "dd/MM/yyyy",
                                            Locale.getDefault()
                                        ).format(
                                            income["Date"]
                                        ), color = MaterialTheme.colorScheme.onBackground
                                    )
                                    Text(
                                        modifier = modifier.verticalScroll(rememberScrollState()),
                                        text = "Descrição: ${income["Description"]}",
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                    Text(
                                        text = "Categoria: ${income["CategoryName"]}",
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                }
                            }

                            if (options) {
                                ReportCompose(homeContext).OptionsIncomeAlert(modifier,
                                    incomeToProcess,
                                    {
                                        navController.navigate("${Screen.Edit.route}/$incomeToProcess") {
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                        }
                                    },
                                    { options = false })
                            }
                        }
                    }
                }
            } else NoIncomesMessage(modifier)
        }
    }

    @Composable
    fun NoIncomesMessage(modifier: Modifier) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(15.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Nenhuma nota criada aperte no icone '+' para adicionar uma nova nota",
                textAlign = TextAlign.Center
            )
        }
    }
}