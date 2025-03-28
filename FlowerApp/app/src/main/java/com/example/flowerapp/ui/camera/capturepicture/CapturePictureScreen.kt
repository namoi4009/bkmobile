package com.example.flowerapp.ui.camera.capturepicture

import android.annotation.SuppressLint
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.LinearLayout
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner

@Composable
fun CapturePictureScreen(

) {
    CameraContent()
}

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
private fun CameraContent() {
    val context = LocalContext.current
    val lifeCycleOwner = LocalLifecycleOwner.current
    val cameraController = remember {
        LifecycleCameraController(context)
    }
    AndroidView(factory = { context ->
        PreviewView(context).apply {
            layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
            scaleType = PreviewView.ScaleType.FILL_START
        } .also { previewView ->
            previewView.controller = cameraController
            cameraController.bindToLifecycle(lifeCycleOwner)
        }
    })
}