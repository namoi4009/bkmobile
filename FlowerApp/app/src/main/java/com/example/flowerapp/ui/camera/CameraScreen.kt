@file:OptIn(ExperimentalPermissionsApi::class)

package com.example.flowerapp.ui.camera

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.flowerapp.R
import com.example.flowerapp.ui.camera.capturepicture.CapturePictureScreen
import com.example.flowerapp.ui.camera.nopermission.NoPermissionScreen
import com.example.flowerapp.ui.theme.CustomScaffold
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@Composable
fun CameraScreen(
    onBackPressed: () -> Unit
) {
    CustomScaffold(
        bottomBarText = stringResource(id = R.string.capture_picture),
        showBackButton = true,
        onBackPressed = onBackPressed
    ) {
        CameraScreenBody()
    }
}

@Composable
fun CameraScreenBody() {
    val cameraPermissionState: PermissionState = rememberPermissionState(android.Manifest.permission.CAMERA)
    CameraScreenControl (
        hasPermission = cameraPermissionState.status.isGranted,
        onRequestPermission = cameraPermissionState::launchPermissionRequest
    )
}

@Composable
private fun CameraScreenControl(
    hasPermission: Boolean,
    onRequestPermission: () -> Unit
) {
    if (hasPermission) {
        CapturePictureScreen()
    } else {
        NoPermissionScreen(onRequestPermission)
    }
}

