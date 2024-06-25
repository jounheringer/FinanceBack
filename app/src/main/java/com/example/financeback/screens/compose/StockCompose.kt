package com.example.financeback.screens.compose

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.financeback.Globals
import com.example.financeback.classes.Stock

class StockCompose(context: Context) {
    private val stockContext = context
    private val stockController = Stock()

    @Composable
    fun StockScreen(modifier: Modifier = Modifier){
        Column(modifier = modifier.fillMaxSize()){
            EnableStocks(modifier = modifier)

//            if (Globals.getStock()){}
            StockInputs(modifier = modifier)
        }
    }

    @Composable
    fun EnableStocks(modifier: Modifier){
        Row(modifier = modifier
            .fillMaxWidth()
            .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center) {
            Text(text = "Habilitar função de estoques")
            Spacer(modifier.width(10.dp))
            Switch(checked = Globals.getStock(), onCheckedChange = { Globals.setStock(!Globals.getStock()) })
        }
    }

    @Composable
    fun StockInputs(modifier: Modifier) {
        Row(modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center) {
            Button(onClick = { /*TODO*/ }) {
                Text(text = "Adicionar nova categoria")
                Spacer(modifier = modifier.width(5.dp))
                Icon(imageVector = Icons.Filled.Add, contentDescription = null)
            }
        }

        CategoryCard(modifier = modifier)
    }

    @Composable
    fun CategoryCard(modifier: Modifier){
        var expands by remember { mutableStateOf(true) }
        var options by remember { mutableStateOf(false) }


        Card(modifier = modifier
            .fillMaxWidth()
            .heightIn(30.dp, 500.dp)
            .padding(12.dp)) {
            Row(modifier = modifier
                .fillMaxWidth()
                .padding(20.dp, 0.dp, 0.dp, 0.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = "Categoria X")
                IconButton(onClick = { expands = !expands }) {
                    if(expands)
                        Icon(Icons.Filled.KeyboardArrowDown, null)
                    else
                        Icon(Icons.Filled.KeyboardArrowUp, null)
                }
            }
            if(expands) {
                Column(
                    modifier = modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    CategoryProducts(modifier = modifier, "Produto", 10, 10.99)
                    CategoryProducts(modifier = modifier, "Produto", 10, 10.99)
                    CategoryProducts(modifier = modifier, "Produto", 10, 10.99)

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Button(onClick = { /*TODO*/ }) {
                            Text(text = "Adicionar produto")
                        }
                        OutlinedButton(
                            onClick = { /*TODO*/ },
                            modifier = modifier.padding(12.dp)
                        ) {
                            Text(text = "Mais")
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun CategoryProducts(modifier: Modifier,
                         name: String,
                         quantity: Int,
                         price: Double){
        var options by remember { mutableStateOf(false) }
        var edit by remember { mutableStateOf(false) }
        var delete by remember { mutableStateOf(false) }

        if(options){
            AlertDialog(onDismissRequest = { options = false },
                title = { Text(text = name) },
                text = { Text(text = "O que deseja fazer com o produto $name")},
                confirmButton = {
                    Row {
                        Button(onClick = { edit = true }) {
                            Text(text = "Editar")
                        }
                        Spacer(modifier = modifier.width(10.dp))
                        Button(onClick = { delete = true }) {
                            Text(text = "Deletar")
                        }
                    }
                })

            if(edit){
                AlertDialog(onDismissRequest = { options = false },
                    title = { Text(text = "Editar $name")},
                    text = { Column {
                        OutlinedTextField(value = "",
                            onValueChange = {},
                            label = { Text(text = "Nome")})
                        Row {
                            OutlinedTextField(value = "5",
                                onValueChange = {},
                                label = { Text(text = "Quantidade") },
                                modifier = modifier.weight(0.4f))
                            Spacer(modifier = modifier.width(5.dp))
                            OutlinedTextField(value = "5",
                                onValueChange = {},
                                label = { Text(text = "Valor") },
                                modifier = modifier.weight(0.6f))
                        }
                    }},
                    confirmButton = { Button(onClick = { options = false }) {
                        Text(text = "Salvar")
                    } },
                    dismissButton = { Button(onClick = { options = false }) {
                        Text(text = "Cancelar")
                    } })
            }

            if(delete){
                AlertDialog(onDismissRequest = { delete = false },
                    title = { Text(text = "Deletar $name")},
                    text = { Text(text = "Deseja deletar $name?")},
                    confirmButton = { Button(onClick = { delete = false }) {
                        Text(text = "Deletar")
                    } },
                    dismissButton = { Button(onClick = { delete = false }) {
                        Text(text = "Cancelar")
                    } })
            }
        }

        Row(modifier = modifier
            .fillMaxWidth()
            .padding(20.dp, 0.dp, 0.dp, 0.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically) {
            Text(text = "Produto X")
            Text(text = "Valor X")
            Text(text = "quantidade X")
            IconButton(onClick = {
                options = true
            }) {
                Icon(
                    imageVector = Icons.Filled.MoreVert,
                    contentDescription = "Opções",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
        }
    }

    @Composable
    fun Options(modifier: Modifier,
                item: String,
                itemSource: String = "") {
        var options by remember { mutableStateOf(false) }
        var edit by remember { mutableStateOf(false) }
        var delete by remember { mutableStateOf(false) }

        if(options){
            AlertDialog(onDismissRequest = { options = false },
                title = { Text(text = item) },
                text = { Text(text = "O que deseja fazer com $item")},
                confirmButton = {
                    Row {
                        Button(onClick = { edit = true }) {
                            Text(text = "Editar")
                        }
                        Spacer(modifier = modifier.width(10.dp))
                        Button(onClick = { delete = true }) {
                            Text(text = "Deletar")
                        }
                    }
                })

            if(edit){
                AlertDialog(onDismissRequest = { options = false },
                    title = { Text(text = "Editar $item")},
                    text = {
                        when (itemSource) {
                            "Prouto" -> Column {
                                OutlinedTextField(value = "",
                                    onValueChange = {},
                                    label = { Text(text = "Nome") })
                                Row {
                                    OutlinedTextField(
                                        value = "5",
                                        onValueChange = {},
                                        label = { Text(text = "Quantidade") },
                                        modifier = modifier.weight(0.4f)
                                    )
                                    Spacer(modifier = modifier.width(5.dp))
                                    OutlinedTextField(
                                        value = "5",
                                        onValueChange = {},
                                        label = { Text(text = "Valor") },
                                        modifier = modifier.weight(0.6f)
                                    )
                                }
                            }
                            "Categoria" -> Column {
                                OutlinedTextField(value = "",
                                    onValueChange = {},
                                    label = { Text(text = "Nome") })
                            }
                            else -> Text(text = "Fonte do item não reconhecida")
                        }
                    },
                    confirmButton = { Button(onClick = { options = false }) {
                        Text(text = "Salvar")
                    } },
                    dismissButton = { Button(onClick = { options = false }) {
                        Text(text = "Cancelar")
                    } })
            }

            if(delete){
                AlertDialog(onDismissRequest = { delete = false },
                    title = { Text(text = "Deletar $item")},
                    text = { Text(text = "Deseja deletar $item?")},
                    confirmButton = { Button(onClick = { delete = false }) {
                        Text(text = "Deletar")
                    } },
                    dismissButton = { Button(onClick = { delete = false }) {
                        Text(text = "Cancelar")
                    } })
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewStockScreen() {
    StockCompose(LocalContext.current).StockScreen()
}