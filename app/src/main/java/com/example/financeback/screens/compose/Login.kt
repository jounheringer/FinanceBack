package com.example.financeback.screens.compose

import android.content.Context
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.financeback.R
import com.example.financeback.classes.DatabaseHelper
import com.example.financeback.classes.User
import com.example.financeback.screens.Credentials
import com.example.financeback.screens.LoginScreen
import com.example.financeback.screens.RegisterScreen

class LoginCompose(context: Context) {
    private val loginContext = context
    private val user = User()
    @Composable
    fun Login(modifier: Modifier = Modifier, activity: ComponentActivity?) {
        Surface(
            modifier = modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                verticalArrangement = Arrangement.SpaceAround,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                WelcomeLogo(modifier)

                LoginInputs(modifier = modifier)
                if (activity != null) {
                    RegisterOption(activity)
                }
            }
        }
    }

    @Composable
    fun WelcomeLogo(modifier: Modifier, screenText: String = "Bem-Vindo") {
        Column(
            modifier = modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.finance_back_logo),
                contentDescription = "Logo"
            )
            Text(
                text = screenText,
                fontSize = 18.sp
            )
        }
    }

    @Composable
    fun LoginInputs(modifier: Modifier) {
        var showPassword by remember { mutableStateOf(false) }
        var credentials by remember { mutableStateOf(Credentials()) }
        var forgotPassword by remember { mutableStateOf(false) }

        if (forgotPassword)
            ForgetPassword(loginContext) { forgotPassword = false }

        Column(
            modifier = modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                modifier = modifier.padding(10.dp),
                text = "Já é um usuario cadastrado?"
            )

            OutlinedTextField(value = credentials.login,
                onValueChange = { data -> credentials = credentials.copy(login = data) },
                label = { Text(text = "Usuario") })

            OutlinedTextField(value = credentials.password,
                onValueChange = { data -> credentials = credentials.copy(password = data) },
                label = { Text(text = "Senha") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { showPassword = !showPassword }) {
                        Icon(
                            painter = painterResource(id = if (showPassword) R.drawable.visibilidade else R.drawable.olho),
                            contentDescription = null
                        )
                    }
                })

            Row(
                modifier = modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = credentials.remember,
                        onCheckedChange = {
                            credentials = credentials.copy(remember = !credentials.remember)
                        })
                    Text(text = "Lembrar login") // TODO remember last login
                }
                TextButton(onClick = { forgotPassword = true }) {
                    Text(text = "Esqueceu sua senha?")
                }
            }

            Button(
                onClick = {
                    if (!LoginScreen().checkCredentials(credentials, loginContext)) credentials =
                        Credentials()
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                enabled = credentials.isNotEmpty()
            ) {
                Text(text = "Login")
            }
        }
    }

    @Composable
    fun ForgetPassword(context: Context, closeAlert: () -> Unit) {
        var credentials by remember { mutableStateOf(Credentials()) }
        var userFound by remember { mutableIntStateOf(-1) }
        var showPassword by remember { mutableStateOf(false) }
        var showTempPassword by remember { mutableStateOf(false) }

        if (userFound >= 0) {
            var tempPassword by remember { mutableStateOf("") }
            AlertDialog(onDismissRequest = { closeAlert() },
                title = { Text(text = "Nova senha") },
                text = {
                    Column {
                        OutlinedTextField(
                            label = { Text(text = "Senha nova") },
                            value = credentials.password,
                            onValueChange = { data ->
                                credentials = credentials.copy(password = data)
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            trailingIcon = {
                                IconButton(onClick = { showPassword = !showPassword }) {
                                    Icon(
                                        painter = painterResource(id = if (showPassword) R.drawable.visibilidade else R.drawable.olho),
                                        contentDescription = null
                                    )
                                }
                            },
                            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                        )
                        OutlinedTextField(
                            label = { Text(text = "Confirme senha") },
                            value = tempPassword,
                            onValueChange = { tempPassword = it },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            trailingIcon = {
                                IconButton(onClick = { showTempPassword = !showTempPassword }) {
                                    Icon(
                                        painter = painterResource(id = if (showTempPassword) R.drawable.visibilidade else R.drawable.olho),
                                        contentDescription = null
                                    )
                                }
                            },
                            isError = !credentials.confirmPassword(tempPassword),
                            visualTransformation = if (showTempPassword) VisualTransformation.None else PasswordVisualTransformation(),
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (user.changePassword(context, userFound, credentials.password, null))
                                closeAlert()
                            else
                                Toast.makeText(context, "Erro ao trocar senha", Toast.LENGTH_SHORT)
                                    .show()
                        },
                        enabled = credentials.confirmPassword(tempPassword)
                    ) {
                        Text(text = "Alterar")
                    }
                },
                dismissButton = {
                    Button(onClick = { closeAlert() }) {
                        Text(text = "Cancelar")
                    }
                })
        }

        AlertDialog(onDismissRequest = { closeAlert() },
            title = { Text(text = "Esqueceu a senha?") },
            text = {
                Column {
                    Text(text = "Digite as informações do usuario")
                    OutlinedTextField(label = { Text(text = "usuario") },
                        value = credentials.login,
                        onValueChange = { data -> credentials = credentials.copy(login = data) })
                    OutlinedTextField(label = { Text(text = "Nome completo") },
                        value = credentials.fullName,
                        onValueChange = { data -> credentials = credentials.copy(fullName = data) })
                }
            },
            confirmButton = {
                Button(onClick = {
                    userFound = user.checkUser(
                        context,
                        mapOf(
                            DatabaseHelper.USERS.COLUMN_NAME to credentials.fullName,
                            DatabaseHelper.USERS.COLUMN_USERNAME to credentials.login
                        )
                    )
                }) {
                    Text(text = "Enviar")
                }
            },
            dismissButton = {
                Button(onClick = { closeAlert() }) {
                    Text(text = "Cancelar")
                }
            })
    }

    @Composable
    fun RegisterOption(activity: ComponentActivity) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "Primeira vez no aplicativo?")
            TextButton(onClick = { LoginScreen().goTo(activity, RegisterScreen::class.java) }) {
                Text(text = "Cadastrar")
            }
        }
    }
}