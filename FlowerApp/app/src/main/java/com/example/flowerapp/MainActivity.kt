package com.example.flowerapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.flowerapp.ui.home.HomeScreen
import com.example.flowerapp.ui.login.LoginScreen
import com.example.flowerapp.ui.theme.FlowerAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MainApp()
        }
    }
}



@Composable
fun MainApp() {
    val navController = rememberNavController()
    FlowerAppTheme {
        Column(
            modifier = Modifier
                .windowInsetsPadding(WindowInsets.systemBars)
                .background(MaterialTheme.colorScheme.background)
                .fillMaxSize()
        ) {
            NavHost(navController = navController, startDestination = "home") {

                // route: login
                composable("login") {
                    LoginScreen(openHomeScreen = {
                        navController.navigate("home")
                    })
                }

                // route: home
                composable("home") {
                    HomeScreen()
                }

            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun GreetingPreview() {
    FlowerAppTheme {
//
    }
}