package com.example.flowerapp.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.example.flowerapp.ui.theme.CustomScaffold
import com.example.flowerapp.ui.theme.MenuCard
import com.example.flowerapp.R
import com.example.flowerapp.ui.theme.CommonVSpace

@Composable
fun HomeScreen(
    openCameraScreen: () -> Unit,
    openView3DModelScreen: () -> Unit
) {
    CustomScaffold(bottomBarText = "Home") {
        innerPadding -> HomeComponent(innerPadding, openCameraScreen, openView3DModelScreen)
    }
}

@Composable
fun HomeComponent(
    innerPadding: PaddingValues,
    openCameraScreen: () -> Unit,
    openView3DModelScreen: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
//            verticalArrangement = Arrangement.SpaceAround
        ) {
            MenuCard(
                imageId = R.drawable.view_3d_model,
                text = stringResource(id = R.string.model_3D),
                onClick = {
                    openView3DModelScreen()
                }
            )
//            CommonVSpace()
            MenuCard(
                imageId = R.drawable.capture_picture,
                text = stringResource(id = R.string.capture_picture),
                onClick = {
                    openCameraScreen()
                }
            )
        }
    }
}