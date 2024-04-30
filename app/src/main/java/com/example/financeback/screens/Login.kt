package com.example.financeback.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material3.AlertDialogDefaults.containerColor
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.financeback.R
import com.example.financeback.utils.Auth
import com.example.financeback.utils.Credentials

@Composable
fun Login(modifier: Modifier = Modifier){
    Surface(modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background) {
        Column(verticalArrangement = Arrangement.SpaceAround,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.finance_back_logo),
                    contentDescription = "Logo"
                )
                Text(text = "Bem-Vindo")
            }

            LoginInputs(modifier = modifier)
        }
    }
}

@Composable
fun LoginInputs(modifier: Modifier) {
    var showPassword by remember { mutableStateOf(false) }
    var credentials by remember { mutableStateOf(Credentials()) }

    val context = LocalContext.current

    Column(modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally) {
        Text(modifier = modifier.padding(10.dp),
            text = "Já é um usuario cadastrado?")

        TextField(value = credentials.login,
            onValueChange = { data -> credentials = credentials.copy(login = data) },
            label = { Text(text = "Usuario") })

        TextField(value = credentials.password,
            onValueChange = { data -> credentials = credentials.copy(password = data) },
            label = { Text(text = "Senha") },
            visualTransformation = if(showPassword) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = { if(!showPassword) IconButton(onClick = { showPassword = true }) {
                Icon(painter = painterResource(id = R.drawable.visibilidade), contentDescription = "Show Password")
            } else IconButton(onClick = { showPassword = false }) {
                Icon(painter = painterResource(id = R.drawable.olho), contentDescription = "Show Password")
            }})

        Row(modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = credentials.remember, onCheckedChange = { credentials = credentials.copy(remember = !credentials.remember) })
                Text(text = "Lembrar login") // TODO remember last login
            }
            TextButton(onClick = { /*TODO reset password*/ }) {
                Text(text = "Esqueceu sua senha?")
            }
        }

        Button(onClick = { if (!Auth().checkCredentials(credentials, context)) credentials = Credentials() },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            enabled = credentials.isNotEmpty()){
            Text(text = "Login")
        }
    }

    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(text = "Primeira vez no aplicativo?")
        TextButton(onClick = {/*TODO register new users */}){
            Text(text = "Cadastrar")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewLoginScreen(){
    Login()
}