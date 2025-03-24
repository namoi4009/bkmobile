package com.example.flowerapp

import android.graphics.Paint
import android.graphics.drawable.Icon
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flowerapp.ui.theme.FlowerAppTheme
import com.example.flowerapp.ui.theme.green_200
import com.example.flowerapp.ui.theme.green_300
import com.example.flowerapp.ui.theme.green_500
import com.example.flowerapp.ui.theme.green_700
import com.example.flowerapp.ui.theme.pink_400
import com.example.flowerapp.ui.theme.pink_600
import com.example.flowerapp.R
import com.example.flowerapp.ui.theme.Pink80
import com.example.flowerapp.ui.theme.green_600
import com.example.flowerapp.ui.theme.pink_200
import com.example.flowerapp.ui.theme.pink_500

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FlowerAppTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    HomeScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = Modifier
            .windowInsetsPadding(WindowInsets.systemBars)
            .background(color = pink_200)
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
                    color = pink_400,
                    shape = RoundedCornerShape(topStart = 50.dp, topEnd = 50.dp)
                )
                .fillMaxWidth()
                .fillMaxHeight()
        ) {
            UsernameTextField()
            CommonVSpace()
            PasswordTextField()
            CommonVSpace()
            LoginButton()
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
            color = pink_600
        )
    )
    Text(
        stringResource(id = R.string.app_description),
        fontStyle = FontStyle.Italic,
        style = TextStyle(
            color = green_600,
            fontWeight = FontWeight.SemiBold
        )
    )
}

@Composable
fun CustomImage(imageId: Int, size: Dp? = null) {
    Image(
        painterResource(id = imageId),
        contentDescription = "logo",
        modifier = size?.let { Modifier.size(it) } ?: Modifier
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
        label = { Text("Username", color = green_700) },
        placeholder = { Text("Enter your username") },
        leadingIcon = { Icon(Icons.Default.Person, contentDescription = "username") },
        trailingIcon = {
            IconButton(onClick = {
                usernameInput = ""
            }) {
                Icon(Icons.Default.Clear, contentDescription = "clear username")
            }
        },
        colors = TextFieldDefaults.colors(
            cursorColor = green_700,
            selectionColors = TextSelectionColors(handleColor = pink_600, backgroundColor = pink_600),

            unfocusedIndicatorColor = Color.Transparent,
            focusedIndicatorColor = green_700,
            disabledIndicatorColor = Color.Transparent,

            focusedLeadingIconColor = green_500,
            unfocusedLeadingIconColor = green_500,
            focusedTrailingIconColor = green_500,
            unfocusedTrailingIconColor = green_500,

            focusedContainerColor = green_200,
            unfocusedContainerColor = green_200,

        ),
        shape = RoundedCornerShape(16.dp),
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Done
        ),

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
        label = { Text("Password", color = green_700) },
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
        colors = TextFieldDefaults.colors(
            cursorColor = green_700,
            selectionColors = TextSelectionColors(handleColor = pink_600, backgroundColor = pink_600),

            unfocusedIndicatorColor = Color.Transparent,
            focusedIndicatorColor = green_700,
            disabledIndicatorColor = Color.Transparent,

            focusedLeadingIconColor = green_500,
            unfocusedLeadingIconColor = green_500,
            focusedTrailingIconColor = green_500,
            unfocusedTrailingIconColor = green_500,

            focusedContainerColor = green_200,
            unfocusedContainerColor = green_200,

            ),
        shape = RoundedCornerShape(16.dp),
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Done
        ),

        )
}

@Composable
fun LoginButton() {
    Button(
        onClick = {},
        colors = ButtonDefaults.buttonColors(green_500),
        contentPadding = PaddingValues(horizontal = 36.dp, vertical = 16.dp)
    ) {
        Text("Log in", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
    }
}

@Composable
fun CommonVSpace(heightValue: Dp = 30.dp) {
    Spacer(modifier = Modifier.height(heightValue))
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun GreetingPreview() {
    FlowerAppTheme {
        HomeScreen()
    }
}