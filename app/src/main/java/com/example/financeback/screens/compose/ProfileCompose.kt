package com.example.financeback.screens.compose

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Create
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.financeback.R
import com.example.financeback.classes.User
import com.example.financeback.classes.UserInfo
import com.example.financeback.screens.LoginScreen
import com.example.financeback.utils.Utils
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

class ProfileCompose (context: Context) {
    private val profileContext = context
    private val user = User()
    @Composable
    fun ProfileScreen(
        modifier: Modifier = Modifier,
        userInfo: UserInfo,
        updatedUser: (Boolean) -> Unit
    ) {
        var edit by remember { mutableStateOf(false) }
        var deleteUser by remember { mutableStateOf(false) }
        var userUpdated by remember { mutableStateOf(false) }
        var changePassword by remember { mutableStateOf(false) }
        val enableEdit = { edit = !edit }
        Column(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            if (deleteUser)
                ConfirmDelete(
                    userInfo = userInfo,
                    deleteUser = { deleteUser = it })
            if (userUpdated) {
                updatedUser(true)
                UserSaved(userUpdated = { userUpdated = it })
            }
            if (changePassword)
                ChangePassword(modifier, userInfo.userID) { changePassword = it }

            ProfileHeader(modifier = modifier, userInfo = userInfo)

            ProfileInfo(
                modifier,
                userInfo,
                edit,
                enableEdit,
                { deleteUser = it },
                { userUpdated = it },
                { changePassword = it })
        }
    }

    // TODO arrumar formatação da foto
    @Composable
    fun ProfileHeader(modifier: Modifier, userInfo: UserInfo) {
        var selectedImage by remember { mutableStateOf<Uri?>(null) }
        var changeProfilePicture by remember { mutableStateOf(false) }
        val imagePicker = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.PickVisualMedia(),
            onResult = { imageUri->
                if (imageUri != null){
                    selectedImage = imageUri
                }
            }
        )

        if (changeProfilePicture) {
            selectedImage?.let {
                ProfilePicture(
                    modifier = modifier,
                    selectedImage = selectedImage,
                    userInfo.userID
                ) {
                    changeProfilePicture = it
                }
            }
        }
        
        Card {
            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(contentAlignment = Alignment.BottomEnd) {
                    IconButton(
                        modifier = modifier.size(100.dp),
                        onClick = { changeProfilePicture = true
                            imagePicker.launch(PickVisualMediaRequest(mediaType = ActivityResultContracts.PickVisualMedia.ImageOnly))}
                    ) {
                        AsyncImage(modifier = modifier
                            .size(200.dp)
                            .clip(CircleShape),
                            model = if (selectedImage != null) ImageRequest.Builder(profileContext)
                                .data(selectedImage)
                                .build()
                            else
                                R.drawable.baseline_person_48,
                            contentDescription = null,
                            contentScale = ContentScale.Crop
                        )
                    }

                    Box(
                        modifier = modifier
                            .clip(CircleShape)
                            .size(24.dp)
                            .background(MaterialTheme.colorScheme.background)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Create,
                            contentDescription = "Editar foto"
                        )
                    }
                }
                Text(text = userInfo.fullName, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text(text = userInfo.userName, fontWeight = FontWeight.Light)
            }
        }

    }

    @Composable
    fun ProfileInfo(
        modifier: Modifier,
        userInfo: UserInfo,
        edit: Boolean = false,
        enableEdit: () -> Unit,
        deleteUser: (Boolean) -> Unit,
        userUpdated: (Boolean) -> Unit,
        changePassword: (Boolean) -> Unit
    ) {
        var editUserInfo by remember { mutableStateOf(userInfo) }
        if (!edit)
            editUserInfo = userInfo
        Column(
            modifier = modifier
                .fillMaxWidth()
                .height(400.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceAround
        ) {
            TextField(
                value = editUserInfo.userName,
                onValueChange = { data -> editUserInfo = editUserInfo.copy(userName = data) },
                label = { Text(text = "Usuario") },
                enabled = edit
            )
            TextField(
                value = editUserInfo.fullName,
                onValueChange = { data -> editUserInfo = editUserInfo.copy(fullName = data) },
                label = { Text(text = "Nome Completo") },
                enabled = edit
            )
            TextField(
                value = SimpleDateFormat(
                    "dd/MM/yyyy",
                    Locale.getDefault()
                ).format(userInfo.dateCreated),
                onValueChange = {},
                label = { Text(text = "Data de criação") },
                enabled = false
            )
            Column(horizontalAlignment = Alignment.End) {
                TextField(
                    value = "********",
                    onValueChange = {},
                    label = { Text(text = "Senha") },
                    enabled = false
                )
                TextButton(onClick = { changePassword(true) }) {
                    Text(
                        text = "Alterar senha",
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
        ProfileOptions(modifier, enableEdit, edit, deleteUser, userUpdated, editUserInfo)
    }

    @Composable
    fun ProfileOptions(
        modifier: Modifier,
        enableEdit: () -> Unit,
        edit: Boolean = true,
        deleteUser: (Boolean) -> Unit,
        userUpdated: (Boolean) -> Unit,
        userInfo: UserInfo
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            if (!edit) {
                CustomButton(
                    { deleteUser(true) },
                    "Deletar Perfil"
                )
                Spacer(modifier.width(24.dp))
                CustomButton(
                    { enableEdit() },
                    "EditarPerfil"
                )
            } else {
                CustomButton(
                    { enableEdit() },
                    "Cancelar"
                )
                Spacer(modifier.width(24.dp))
                CustomButton(
                    { userUpdated(user.editUser(profileContext, userInfo)) },
                    "Salvar Perfil"
                )
            }
        }
    }

    @Composable
    fun CustomButton(
        onClick: () -> Unit,
        text: String,
        colors: ButtonColors = ButtonColors(
            MaterialTheme.colorScheme.onPrimary,
            MaterialTheme.colorScheme.onBackground,
            MaterialTheme.colorScheme.errorContainer,
            MaterialTheme.colorScheme.error
        )
    ) {
        Button(
            onClick = { onClick() },
            colors = colors
        ) {
            Text(text = text)
        }
    }

    @Composable
    fun ConfirmDelete(deleteUser: (Boolean) -> Unit, userInfo: UserInfo) {
        var userCanceled by remember { mutableStateOf(false) }

        AlertDialog(onDismissRequest = { deleteUser(false) },
            text = { Text(text = "Deseja deletar seu perfil?\n(Fazendo isso seu perfil e notas salvas serão deletadas para sempre)") },
            confirmButton = {
                Button(onClick = {
                    userCanceled = user.deleteUser(profileContext, userInfo)
                }) {
                    Text(text = "Deletar")
                }
            },
            dismissButton = {
                Button(onClick = { deleteUser(false) }) {
                    Text(text = "Cancelar")
                }
            })
        if (userCanceled) {
            AlertDialog(onDismissRequest = { },
                confirmButton = {
                    Button(onClick = {
                        Utils().logout(
                            profileContext,
                            LoginScreen::class.java,
                            "LogOut"
                        )
                    }) {
                        Text(text = "Ok")
                    }
                },
                text = { Text(text = "Perfil cancelado") })
        }
    }

    @Composable
    fun UserSaved(userUpdated: (Boolean) -> Unit) {
        AlertDialog(onDismissRequest = { userUpdated(false) },
            confirmButton = {
                Button(onClick = { userUpdated(false) }) {
                    Text(text = "OK")
                }
            },
            text = { Text(text = "Perfil atualizado com sucesso") })
    }

    @Composable
    fun ChangePassword(modifier: Modifier, userID: Int, changePassword: (Boolean) -> Unit) {
        var oldPassword by remember { mutableStateOf("") }
        var newPassword by remember { mutableStateOf("") }
        var newPasswordConfirm by remember { mutableStateOf("") }
        var userSaved by remember { mutableStateOf(false) }
        var showOldPassword by remember { mutableStateOf(false) }
        var showNewPassword by remember { mutableStateOf(false) }
        var showNewPasswordConfirm by remember { mutableStateOf(false) }
        val context = LocalContext.current
        val user = User()
        AlertDialog(onDismissRequest = { changePassword(false) },
            title = { Text(text = "Alterar Senha") },
            text = {
                Column(
                    modifier = modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    verticalArrangement = Arrangement.SpaceAround
                ) {
                    OutlinedTextField(value = oldPassword,
                        onValueChange = { oldPassword = it },
                        label = { Text(text = "Senha antiga") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        visualTransformation = if (!showOldPassword) PasswordVisualTransformation() else VisualTransformation.None,
                        trailingIcon = {
                            IconButton(onClick = { showOldPassword = !showOldPassword }) {
                                Icon(
                                    painter = if (!showOldPassword)
                                        painterResource(id = R.drawable.olho)
                                    else painterResource(id = R.drawable.visibilidade),
                                    contentDescription = null
                                )
                            }
                        })
                    OutlinedTextField(value = newPassword,
                        onValueChange = { newPassword = it },
                        label = { Text(text = "Senha Nova") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        visualTransformation = if (!showNewPassword) PasswordVisualTransformation() else VisualTransformation.None,
                        trailingIcon = {
                            IconButton(onClick = { showNewPassword = !showNewPassword }) {
                                Icon(
                                    painter = if (!showNewPassword)
                                        painterResource(id = R.drawable.olho)
                                    else painterResource(id = R.drawable.visibilidade),
                                    contentDescription = null
                                )
                            }
                        })
                    OutlinedTextField(
                        value = newPasswordConfirm,
                        onValueChange = { newPasswordConfirm = it },
                        label = { Text(text = "Confirmar senha nova") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        visualTransformation = if (!showNewPasswordConfirm) PasswordVisualTransformation() else VisualTransformation.None,
                        isError = newPassword.isNotEmpty() && newPassword != newPasswordConfirm,
                        trailingIcon = {
                            IconButton(onClick = {
                                showNewPasswordConfirm = !showNewPasswordConfirm
                            }) {
                                Icon(
                                    painter = if (!showNewPasswordConfirm)
                                        painterResource(id = R.drawable.olho)
                                    else painterResource(id = R.drawable.visibilidade),
                                    contentDescription = null
                                )
                            }
                        }
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (user.changePassword(context, userID, newPassword, oldPassword))
                            userSaved = true
                        else
                            Toast.makeText(
                                context,
                                "Senha antiga esta errada tente novamente",
                                Toast.LENGTH_SHORT
                            ).show()
                    },
                    enabled = oldPassword.isNotEmpty() && newPassword.isNotEmpty() && newPassword == newPasswordConfirm
                ) {
                    Text(text = "Alterar senha")
                }
            },
            dismissButton = {
                Button(onClick = { changePassword(false) }) {
                    Text(text = "Cancelar")
                }
            })

        if (userSaved) {
            AlertDialog(onDismissRequest = { }, confirmButton = {
                Button(onClick = { changePassword(false) }) {
                    Text(text = "OK")
                }
            },
                text = { Text(text = "Senha alterada com sucesso") })
        }
    }

//    TODO salvar fotos baseado no id do usuario e dentro da pasta do app no celular do usuario
    @Composable
    fun ProfilePicture(modifier: Modifier,
                       selectedImage: Uri?,
                       userID: Int,
                       changeProfilePicture: (Boolean) -> Unit) {
        val profilePicture: File? =
            selectedImage?.let {
                Utils().getFileFromURI(this.profileContext, it, userID.toString())
            }

        AlertDialog(
            onDismissRequest = { },
            title = { Text(text = "Alterar foto de perfil")},
            text = { Column(modifier = modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally){
                Text(text = "Deseja adicionar esta foto ao seu perfil?")
                AsyncImage(
                    modifier = modifier
                        .size(200.dp)
                        .clip(CircleShape),
                    model = if (selectedImage != null)
                            ImageRequest.Builder(profileContext)
                            .data(selectedImage)
                            .build()
                        else
                            R.drawable.baseline_person_48,
                    contentDescription = null,
                    contentScale = ContentScale.Crop
                )
            }},
            confirmButton = { Button(onClick = { if(profilePicture != null) this.SaveProfilePicture(profilePicture)
                changeProfilePicture(false) }) {
                Text(text = "Confirmar")
            } },
            dismissButton = { OutlinedButton(onClick = { changeProfilePicture(false) }
            ) {
                Text(text = "Cancelar")
            } })
    }

    private fun SaveProfilePicture(profilePicture: File?) {

    }

    @Composable
    private fun CheckPermissionAndPickPhoto() {
        val cameraPermissionLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                // Permission is granted, proceed to step 2

            } else {
                // Permission is denied, handle it accordingly
            }
        }
    }
}