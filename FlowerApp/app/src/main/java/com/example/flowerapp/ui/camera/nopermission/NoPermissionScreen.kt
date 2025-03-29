package com.example.flowerapp.ui.camera.nopermission

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flowerapp.R
import com.example.flowerapp.ui.theme.ButtonWithIcon
import com.example.flowerapp.ui.theme.CustomImage

@Composable
fun NoPermissionScreen(onRequestPermission: () -> Unit) {
    NoPermissionScreenBody(onRequestPermission)
}

@Composable
private fun NoPermissionScreenBody(
    onRequestPermission: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            CustomImage(imageId = R.drawable.permission_frame)
            Text(
                text = stringResource(id = R.string.grant_camera_permission_message),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(70.dp),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        ButtonWithIcon(
            modifier = Modifier.padding(20.dp),
            onClick = onRequestPermission,
            iconId = R.drawable.photo_camera,
            textId = R.string.grant_permission,
        )
    }
}