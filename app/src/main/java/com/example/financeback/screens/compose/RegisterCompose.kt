package com.example.financeback.screens.compose

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.financeback.R
import com.example.financeback.screens.Credentials
import com.example.financeback.screens.LoginScreen
import com.example.financeback.screens.RegisterScreen

class RegisterCompose (context: Context) {
    private val registerContext= context
    @Composable
    fun Register(modifier: Modifier = Modifier, activity: ComponentActivity?) {
        Surface(
            modifier = modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            if (activity != null) {
                BackToLogin(modifier, activity)
            }
            Column(
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LoginCompose(registerContext).WelcomeLogo(modifier, "Cadastro de usuario")

                RegisterInputs(modifier = modifier)
            }
        }
    }

    @Composable
    fun RegisterInputs(modifier: Modifier) {
        var showPassword by remember { mutableStateOf(false) }
        var showTempPassword by remember { mutableStateOf(false) }
        var userInfo by remember { mutableStateOf(Credentials()) }
        var tempPassword by remember { mutableStateOf("") }

        Column(
            modifier = modifier
                .size(280.dp, 350.dp)
                .imePadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            OutlinedTextField(value = userInfo.login,
                onValueChange = { data -> userInfo = userInfo.copy(login = data) },
                label = { Text(text = "Usuario") })

            OutlinedTextField(value = userInfo.fullName,
                onValueChange = { data -> userInfo = userInfo.copy(fullName = data) },
                label = { Text(text = "Nome Completo") })

//        TODO add more password safety

            OutlinedTextField(value = userInfo.password,
                onValueChange = { data -> userInfo = userInfo.copy(password = data) },
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

            OutlinedTextField(value = tempPassword,
                onValueChange = { tempPassword = it },
                label = { Text(text = "Confirmar senha") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation = if (showTempPassword) VisualTransformation.None else PasswordVisualTransformation(),
                isError = !userInfo.confirmPassword(tempPassword),
                trailingIcon = {
                    IconButton(onClick = { showTempPassword = !showTempPassword }) {
                        Icon(
                            painter = painterResource(id = if (showTempPassword) R.drawable.visibilidade else R.drawable.olho),
                            contentDescription = null
                        )
                    }
                })

            Button(
                onClick = {
                    if (!RegisterScreen().checkRegister(userInfo, registerContext)) userInfo = Credentials()
                },
                modifier = modifier.fillMaxWidth(),
                enabled = (userInfo.isNotEmpty() && userInfo.confirmPassword(tempPassword))
            ) {
                Text(text = "Cadastrar")
            }
        }
    }

    @Composable
    fun BackToLogin(modifier: Modifier, activity: ComponentActivity) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(15.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.Start
        ) {
            IconButton(onClick = {
                LoginScreen().goTo(
                    activity,
                    LoginScreen::class.java,
                    "FromRegister"
                )
            }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Voltar"
                )
            }
        }
    }
}