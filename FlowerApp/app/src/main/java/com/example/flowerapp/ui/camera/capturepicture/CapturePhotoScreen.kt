package com.example.flowerapp.ui.camera.capturepicture

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Matrix
import android.net.Uri
import android.util.Log
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.LinearLayout
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import org.koin.androidx.compose.koinViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.flowerapp.R
import kotlinx.coroutines.delay
import java.util.concurrent.Executor

@Composable
fun CapturePictureScreen(
    viewModel: CameraViewModel = koinViewModel()
) {
    val cameraState: CameraState = viewModel.state.collectAsStateWithLifecycle().value

    CameraContent(
        onPhotoCaptured = viewModel::storePhotoInGallery,
        lastCapturedPhoto = cameraState.capturedImage
    )
}

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
private fun CameraContent(
    onPhotoCaptured: (Bitmap) -> Unit,
    lastCapturedPhoto: Bitmap? = null,
    viewModel: CameraViewModel = koinViewModel()  // Add viewModel parameter
) {
    val context = LocalContext.current
    val lifeCycleOwner = LocalLifecycleOwner.current
    val cameraController = remember {
        LifecycleCameraController(context)
    }

    // State to control the flash effect
    var isFlashing by remember { mutableStateOf(false) }

    // State to control the visibility of the last photo preview
    var showLastPhotoPreview by remember { mutableStateOf(lastCapturedPhoto != null) }

    var isFrontCamera by remember { mutableStateOf(false) }
    fun toggleCamera() {
        isFrontCamera = !isFrontCamera
        cameraController.cameraSelector = if (isFrontCamera) {
            CameraSelector.DEFAULT_FRONT_CAMERA
        } else {
            CameraSelector.DEFAULT_BACK_CAMERA
        }
    }

    Scaffold (
        floatingActionButton = {
            Box(contentAlignment = Alignment.BottomCenter, modifier = Modifier.fillMaxSize()) {
                // Capture Button
                FloatingActionButton(
                    onClick = {
                        capturePhoto(context, cameraController) { bitmap ->
                            onPhotoCaptured(bitmap)
                            showLastPhotoPreview = true
                        }
                    },
                    shape = CircleShape,
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier
                        .size(80.dp)
                        .align(Alignment.BottomCenter)
                ) {
                    Icon(
                        painterResource(R.drawable.camera),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(0.7f)
                    )
                }

                // Toggle Camera Button
                FloatingActionButton(
                    onClick = { toggleCamera() },
                    shape = CircleShape,
                    containerColor = MaterialTheme.colorScheme.onSecondary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier
                        .padding(end = 24.dp)
                        .size(60.dp)
                        .align(Alignment.BottomEnd)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.flip_camera_android),
                        contentDescription = "Switch Camera",
                        modifier = Modifier.fillMaxSize(0.7f)
                    )
                }
            }
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { innerPadding ->
        Box(
            modifier = Modifier.padding(innerPadding)
        ){
            AndroidView(factory = { context ->
                PreviewView(context).apply {
                    layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
                    scaleType = PreviewView.ScaleType.FILL_START
                }.also { previewView ->
                    previewView.controller = cameraController
                    cameraController.bindToLifecycle(lifeCycleOwner)
                }
            })

            // Flash effect overlay
            FlashEffect(isVisible = isFlashing)

            // Reset flash state after it's complete
            LaunchedEffect(isFlashing) {
                if (isFlashing) {
                    delay(200)
                    isFlashing = false
                }
            }

            // Show the last photo preview if we have a photo and the preview is enabled
            if (lastCapturedPhoto != null && showLastPhotoPreview) {
                LastPhotoPreview(
                    modifier = Modifier.align(Alignment.BottomStart),
                    lastCapturedPhoto = lastCapturedPhoto,
                    onDismiss = {
                        showLastPhotoPreview = false
                    },
                    onViewFullPhoto = {
                        // Open the photo in the gallery using the URI from the ViewModel
                        openPhotoInGallery(context, viewModel.getLastPhotoUri())
                    }
                )
            }
        }
    }
}

private fun capturePhoto(
    context: Context,
    cameraController: LifecycleCameraController,
    onPhotoCaptured: (Bitmap) -> Unit
) {
    val mainExecutor: Executor = ContextCompat.getMainExecutor(context)

    cameraController.takePicture(
        mainExecutor,
        object: ImageCapture.OnImageCapturedCallback() {
            override fun onCaptureSuccess(image: ImageProxy) {
                val matrix = Matrix().apply {
                    postRotate(image.imageInfo.rotationDegrees.toFloat())
                }
                val correctedBitmap = Bitmap.createBitmap(
                    image.toBitmap(),
                    0,
                    0,
                    image.width,
                    image.height,
                    matrix,
                    true
                )
                onPhotoCaptured(correctedBitmap)
                image.close()
            }

            override fun onError(exception: ImageCaptureException) {
                Log.e("CameraContent", "Error capturing image", exception)
            }
        })
}

@Composable
private fun LastPhotoPreview(
    modifier: Modifier = Modifier,
    lastCapturedPhoto: Bitmap,
    onDismiss: () -> Unit,
    onViewFullPhoto: () -> Unit
) {
    // Create a Box that covers the entire screen to detect taps outside the image
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null // No ripple effect
            ) {
                // When tapped outside, dismiss the preview
                onDismiss()
            }
    ) {
        val capturedPhoto: ImageBitmap = remember(lastCapturedPhoto.hashCode()) {
            lastCapturedPhoto.asImageBitmap()
        }

        Card(
            modifier = modifier
                .size(128.dp)
                .padding(16.dp)
                // Stop clicks on the card from reaching the background Box
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = ripple()
                ) {
                    // When tapped on the image, view full photo in gallery
                    onViewFullPhoto()
                },
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            shape = MaterialTheme.shapes.large,
            // Add a white border around the card
            border = BorderStroke(width = 2.dp, color = Color.White)
        ) {
            Image(
                bitmap = capturedPhoto,
                contentDescription = "Last captured photo",
                contentScale = ContentScale.Crop
            )
        }
    }
}

@Composable
fun FlashEffect(isVisible: Boolean) {
    var showFlash by remember { mutableStateOf(isVisible) }

    // When isVisible becomes true, we show the flash and then hide it after a delay
    LaunchedEffect(isVisible) {
        if (isVisible) {
            showFlash = true
            delay(150) // Flash duration
            showFlash = false
        }
    }

    val alpha by animateFloatAsState(
        targetValue = if (showFlash) 0.7f else 0f,
        animationSpec = tween(
            durationMillis = if (showFlash) 50 else 100
        ),
        label = "flash_animation"
    )

    if (alpha > 0) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .alpha(alpha)
                .background(Color.White)
        )
    }
}

private fun openPhotoInGallery(context: Context, uri: Uri?) {
    if (uri != null) {
        try {
            Log.d("CameraDebug", "Opening photo with URI: $uri")

            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "image/jpeg")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            val chooser = Intent.createChooser(intent, "Open with")
            context.startActivity(chooser)
        } catch (e: Exception) {
            Log.e("CameraDebug", "Error opening image: ${e.message}")
            Toast.makeText(context, "Error opening image", Toast.LENGTH_SHORT).show()
        }
    } else {
        Log.e("CameraDebug", "Image not available")
        Toast.makeText(context, "Image not available", Toast.LENGTH_SHORT).show()
    }
}
