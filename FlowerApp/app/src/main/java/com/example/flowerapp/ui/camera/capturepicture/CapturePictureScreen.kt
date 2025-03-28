package com.example.flowerapp.ui.camera.capturepicture

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.LinearLayout
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.flowerapp.R

@Composable
fun CapturePictureScreen(

) {
    CameraContent()
}

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
private fun CameraContent(

) {
    val context = LocalContext.current
    val lifeCycleOwner = LocalLifecycleOwner.current
    val cameraController = remember {
        LifecycleCameraController(context)
    }
    Scaffold (
        floatingActionButton = {
            FloatingActionButton(
                onClick = {},
                shape = CircleShape,
                containerColor = MaterialTheme.colorScheme.onSecondary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(80.dp)
            ) {
                Icon(
                    painterResource(R.drawable.filter_vintage),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(0.7f)
                )
            }
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { innerPadding ->
        AndroidView(factory = { context ->
            PreviewView(context).apply {
                layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
                scaleType = PreviewView.ScaleType.FILL_START
            } .also { previewView ->
                previewView.controller = cameraController
                cameraController.bindToLifecycle(lifeCycleOwner)
            }
        })

        Row(
            modifier = Modifier.padding(innerPadding)
        ) {
            //
        }
    }
}