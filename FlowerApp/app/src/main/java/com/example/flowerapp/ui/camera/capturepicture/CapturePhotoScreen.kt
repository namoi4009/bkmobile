package com.example.flowerapp.ui.camera.capturepicture

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.RectF
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
import androidx.camera.view.TransformExperimental
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
import androidx.compose.runtime.mutableStateListOf
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import org.koin.androidx.compose.koinViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.flowerapp.R
import com.example.flowerapp.ui.theme.CustomFloatingActionButton
import kotlinx.coroutines.delay
import java.util.concurrent.Executor
import androidx.core.graphics.scale
import androidx.core.graphics.createBitmap

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

    var selectedStickers = remember { mutableStateListOf<Sticker>() }

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

    var previewView: PreviewView? by remember { mutableStateOf(null) }

    Scaffold (
        floatingActionButton = {
            Box(contentAlignment = Alignment.BottomCenter, modifier = Modifier.fillMaxSize()) {
                // Capture Button
                CustomFloatingActionButton(
                    onClick = {
                        capturePhoto(context, cameraController, selectedStickers, previewView) { bitmap ->
                            onPhotoCaptured(bitmap)
                            showLastPhotoPreview = true
                            isFlashing = true
                        }
                    },
                    iconId = R.drawable.camera,
                    modifier = Modifier
                        .size(80.dp)
                        .align(Alignment.BottomCenter)
                )

                // Toggle Camera Button
                CustomFloatingActionButton(
                    onClick = {
                        toggleCamera()
                    },
                    primaryButton = false,
                    iconId = R.drawable.flip_camera_android,
                    modifier = Modifier
                        .padding(end = 24.dp)
                        .size(60.dp)
                        .align(Alignment.BottomEnd)
                )
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
                }.also { view ->
                    previewView = view
                    view.controller = cameraController
                    cameraController.bindToLifecycle(lifeCycleOwner)
                }
            })

            StickerCanvas(stickers = selectedStickers, modifier = Modifier.fillMaxSize())

            ToggleLazyColumnScreen { sticker ->
                selectedStickers.add(sticker.copy())

            }

            // Flash effect overlay
            FlashEffect(isVisible = isFlashing)

            // Reset flash state after it's complete
            LaunchedEffect(isFlashing) {
                if (isFlashing) {
                    delay(200)
                    isFlashing = false
                }
            }

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
    stickers: List<Sticker>,
    previewView: PreviewView?,
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
                val cameraBitmap = Bitmap.createBitmap(
                    image.toBitmap(),
                    0,
                    0,
                    image.width,
                    image.height,
                    matrix,
                    true
                )
                val previewWidth = previewView?.width ?: cameraBitmap.width
                val previewHeight = previewView?.height ?: cameraBitmap.height

                val finalBitmap = mergeStickersOnBitmap(
                    context, cameraBitmap, stickers, previewView)

                onPhotoCaptured(finalBitmap)
                image.close()
            }

            override fun onError(exception: ImageCaptureException) {
                Log.e("CameraContent", "Error capturing image", exception)
            }
        })
}

private fun mergeStickersOnBitmap(
    context: Context,
    cameraBitmap: Bitmap,
    stickers: List<Sticker>,
    previewView: PreviewView?
): Bitmap {
    val resultBitmap = cameraBitmap.copy(Bitmap.Config.ARGB_8888, true)
    val canvas = Canvas(resultBitmap)

    previewView?.let { preview ->
        val previewWidth = preview.width.toFloat()
        val previewHeight = preview.height.toFloat()
        val cameraWidth = cameraBitmap.width.toFloat()
        val cameraHeight = cameraBitmap.height.toFloat()

        val density = context.resources.displayMetrics.density

        // Compute aspect-ratio-aware scaling factors
        val scaleX = cameraWidth / previewWidth
        val scaleY = cameraHeight / previewHeight

        val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        stickers.forEach { sticker ->
            val stickerBitmap = BitmapFactory.decodeResource(context.resources, sticker.imageId)

            // Convert dp size to pixels correctly
            val stickerSizePx = (100 * sticker.scale * density).toInt()

            // Ensure the sticker maintains the correct scaling when placed
            val scaledSticker = stickerBitmap.scale(stickerSizePx, stickerSizePx)

            // Map sticker position correctly from preview space to camera space
            val stickerX = (sticker.x * scaleX) - (stickerSizePx / 2)
            val stickerY = (sticker.y * scaleY) - (stickerSizePx / 2)

            canvas.drawBitmap(scaledSticker, stickerX, stickerY, paint)

            stickerBitmap.recycle()
        }
    }

    return resultBitmap
}

@Composable
private fun LastPhotoPreview(
    modifier: Modifier = Modifier,
    lastCapturedPhoto: Bitmap,
    onDismiss: () -> Unit,
    onViewFullPhoto: () -> Unit
) {
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

    LaunchedEffect(isVisible) {
        if (isVisible) {
            showFlash = true
            delay(150)
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