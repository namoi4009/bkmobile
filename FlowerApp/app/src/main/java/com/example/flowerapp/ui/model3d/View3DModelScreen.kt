package com.example.flowerapp.ui.model3d

import android.view.MotionEvent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.flowerapp.R
import com.example.flowerapp.ui.theme.ButtonWithIcon
import com.example.flowerapp.ui.theme.CustomScaffold
import dev.romainguy.kotlin.math.Float3
import io.github.sceneview.Scene
import io.github.sceneview.collision.HitResult
import io.github.sceneview.math.Position
import io.github.sceneview.math.Rotation
import io.github.sceneview.node.ModelNode
import io.github.sceneview.rememberCameraManipulator
import io.github.sceneview.rememberCameraNode
import io.github.sceneview.rememberCollisionSystem
import io.github.sceneview.rememberEngine
import io.github.sceneview.rememberEnvironment
import io.github.sceneview.rememberEnvironmentLoader
import io.github.sceneview.rememberMainLightNode
import io.github.sceneview.rememberMaterialLoader
import io.github.sceneview.rememberModelLoader
import io.github.sceneview.rememberNodes
import io.github.sceneview.rememberOnGestureListener
import io.github.sceneview.rememberRenderer
import io.github.sceneview.rememberScene
import io.github.sceneview.rememberView
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun View3DModelScreen() {
    CustomScaffold(
        bottomBarText = "View 3D Model"
    ) {
        GLBModelViewer(modelResourceId = R.raw.rose)
    }
}

@Composable
fun GLBModelViewer(modelResourceId: Int) {
    val engine = rememberEngine()
    val view = rememberView(engine)
    val renderer = rememberRenderer(engine)
    val scene = rememberScene(engine)
    val modelLoader = rememberModelLoader(engine)
    val materialLoader = rememberMaterialLoader(engine)
    val environmentLoader = rememberEnvironmentLoader(engine)
    val collisionSystem = rememberCollisionSystem(view)

    var showPopup by remember { mutableStateOf(false) }
    val modelNode = remember {
        ModelNode(
            modelInstance = modelLoader.createModelInstance(
                rawResId = modelResourceId
            ),
            scaleToUnits = 1.0f
        )
    }

    var rotationAngle by remember { mutableFloatStateOf(0f) }
    val initialScale = remember { modelNode.scale }
    var modelPosition by remember { mutableStateOf(Position(0f, 0f, 0f)) }
    var cameraNode = rememberCameraNode(engine) {
        position = Position(z = 4.0f)
    }
    val initialCameraPosition = remember { cameraNode.position }

    // Rotate the model on its own Y-axis
    LaunchedEffect(Unit) {
        while (true) {
            delay(16L) // ~60 FPS
            rotationAngle += 1f // Adjust speed of rotation (1f = slower, 5f = faster)

            modelNode.rotation = Rotation(y = rotationAngle)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scene(
            modifier = Modifier.fillMaxSize(),
            engine = engine,
            view = view,
            renderer = renderer,
            scene = scene,
            modelLoader = modelLoader,
            materialLoader = materialLoader,
            environmentLoader = environmentLoader,
            collisionSystem = collisionSystem,
            isOpaque = true,
            mainLightNode = rememberMainLightNode(engine) {
                intensity = 100_000.0f
            },
            environment = rememberEnvironment(environmentLoader) {
                environmentLoader.createHDREnvironment(
                    rawResId = R.raw.neutral
                )!!
            },
            cameraNode = cameraNode,
            cameraManipulator = rememberCameraManipulator(),
            childNodes = rememberNodes {
                add(modelNode)
            },
            onGestureListener = rememberOnGestureListener(
                onDoubleTapEvent = { _, tapedNode ->
                    if (tapedNode != null) {
                        showPopup = true
                    }
                }
            ),
            onTouchEvent = { _, _ -> false }
        )

        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(32.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Joystick(
                modifier = Modifier.padding(end = 16.dp),
                onMove = { dx, dy ->
                    modelPosition = Position(modelPosition.x + dx * 0.1f, modelPosition.y - dy * 0.1f, modelPosition.z)
                    modelNode.position = modelPosition
                }
            )

            ButtonWithIcon(
                iconId = R.drawable.reset,
                textId = R.string.reset,
                onClick = {
                    modelNode.position = Position(0f, 0f, 0f)
                    modelNode.scale = initialScale
                    cameraNode.position = initialCameraPosition
                },
                primaryButton = false
            )
        }

        if (showPopup) {
            ModelInfoPopup(onDismiss = { showPopup = false })
        }

        ModelLazyRow()
    }
}

@Composable
fun ModelInfoPopup(onDismiss: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable(onClick = onDismiss), // Dismiss when tapped outside
        contentAlignment = Alignment.Center // Center the popup above the model
    ) {
        Card(
            modifier = Modifier
                .padding(24.dp)
                .align(Alignment.Center), // Aligns the pop-up above the 3D model
            shape = MaterialTheme.shapes.medium,
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Rose in Glass",
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "A beautiful red rose preserved in a glass dome, symbolizing love and eternity.",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = onDismiss) {
                    Text("Close")
                }
            }
        }
    }
}

@Composable
fun Joystick(
    modifier: Modifier = Modifier,
    onMove: (Float, Float) -> Unit
) {
    val joystickRadius = 50f // Outer circle radius
    val thumbRadius = 20f // Inner thumb size
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }

    Box(
        modifier = modifier
            .size(joystickRadius.dp * 2)
            .background(color = MaterialTheme.colorScheme.primary, shape = CircleShape)
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    offsetX = (offsetX + dragAmount.x).coerceIn(-joystickRadius, joystickRadius)
                    offsetY = (offsetY + dragAmount.y).coerceIn(-joystickRadius, joystickRadius)
                    onMove(offsetX / joystickRadius, offsetY / joystickRadius)
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(thumbRadius.dp * 2)
                .offset { IntOffset(offsetX.toInt(), offsetY.toInt()) }
                .background(MaterialTheme.colorScheme.onPrimary, shape = CircleShape)
                .pointerInput(Unit) {
                    detectDragGestures(onDragEnd = {
                        offsetX = 0f
                        offsetY = 0f
                        onMove(0f, 0f) // Reset movement
                    }) { change, dragAmount ->
                        change.consume()
                        offsetX = (offsetX + dragAmount.x).coerceIn(-joystickRadius, joystickRadius)
                        offsetY = (offsetY + dragAmount.y).coerceIn(-joystickRadius, joystickRadius)
                        onMove(offsetX / joystickRadius, offsetY / joystickRadius)
                    }
                }
        )
    }
}

@Composable
fun ModelLazyRow() {
    LazyRow (
        userScrollEnabled = true
    ) {
        item {
            Text(
                text = "Item 1",
                modifier = Modifier.padding(16.dp)
            )
        }
        item {
            Text(
                text = "Item 2",
                modifier = Modifier.padding(16.dp)
            )
        }
        item {
            Text(
                text = "Item 3",
                modifier = Modifier.padding(16.dp)
            )
        }
        item {
            Text(
                text = "Item 3",
                modifier = Modifier.padding(16.dp)
            )
        }
        item {
            Text(
                text = "Item 3",
                modifier = Modifier.padding(16.dp)
            )
        }
        item {
            Text(
                text = "Item 3",
                modifier = Modifier.padding(16.dp)
            )
        }
        item {
            Text(
                text = "Item 3",
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}
