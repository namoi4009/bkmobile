package com.example.flowerapp.ui.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flowerapp.R
import com.example.flowerapp.ui.theme.CommonVSpace
import com.example.flowerapp.ui.theme.CustomImage
import com.example.flowerapp.ui.theme.customTextFieldColors

@Composable
fun LoginScreen(openHomeScreen: () -> Unit) {
    Column (
        modifier = Modifier.fillMaxHeight()
    ) {
        Row (
            modifier = Modifier.padding(15.dp)
        ) {
            CustomImage(R.drawable.bk_name_en)
        }
        Column (
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 20.dp)
        ) {
            CustomImage(R.drawable.flower_app_logo, size = 250.dp)
            AppName()
        }
        Column (
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(topStart = 50.dp, topEnd = 50.dp)
                )
                .fillMaxWidth()
                .fillMaxHeight()
        ) {
            UsernameTextField()
            CommonVSpace()
            PasswordTextField()
            CommonVSpace()
            LoginButton(openHomeScreen)
        }
    }
}

@Composable
fun AppName() {
    Text(
        stringResource(id = R.string.app_name),
        style = TextStyle(
            fontSize = 40.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.tertiary
        )
    )
    Text(
        stringResource(id = R.string.app_description),
        fontStyle = FontStyle.Italic,
        style = TextStyle(
            color = MaterialTheme.colorScheme.onSecondaryContainer,
            fontWeight = FontWeight.SemiBold
        )
    )
}

@Composable
fun UsernameTextField() {
    var usernameInput by remember {
        mutableStateOf("")
    }
    TextField(
        value = usernameInput,
        onValueChange = {
                newValue -> usernameInput = newValue
        },
        label = { Text("Username", color = MaterialTheme.colorScheme.onSurface) },
        placeholder = { Text("Enter your username") },
        leadingIcon = { Icon(Icons.Default.Person, contentDescription = "username") },
        trailingIcon = {
            IconButton(onClick = {
                usernameInput = ""
            }) {
                Icon(Icons.Default.Clear, contentDescription = "clear username")
            }
        },
        colors = customTextFieldColors(),

        )
}

@Composable
fun PasswordTextField() {
    var passwordInput by remember {
        mutableStateOf("")
    }

    var isShowPassword by remember {
        mutableStateOf(false)
    }

    TextField(
        value = passwordInput,
        onValueChange = {
                newValue -> passwordInput = newValue
        },
        label = { Text("Password", color = MaterialTheme.colorScheme.onSurface) },
        placeholder = { Text("Enter your password") },
        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "password") },
        trailingIcon = {
            IconButton(onClick = {
                isShowPassword = !isShowPassword
            }) {
                if (isShowPassword)
                    Icon(
                        painter = painterResource(id = R.drawable.visibility),
                        contentDescription = null
                    )
                else
                    Icon(
                        painter = painterResource(id = R.drawable.visibility_off),
                        contentDescription = null
                    )
            }
        },
        visualTransformation = if (isShowPassword) VisualTransformation.None else PasswordVisualTransformation(),
        colors = customTextFieldColors(),
        shape = RoundedCornerShape(16.dp),
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Done
        ),

        )
}

@Composable
fun LoginButton(openHomeScreen: () -> Unit) {
    Button(
        onClick = {
            openHomeScreen()
        },
        colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.secondary),
        contentPadding = PaddingValues(horizontal = 36.dp, vertical = 16.dp)
    ) {
        Text("Log in", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
    }
}