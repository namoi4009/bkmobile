package com.example.flowerapp.ui.model3d

import android.view.MotionEvent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.ButtonDefaults.buttonColors
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardDefaults.cardColors
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
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

@Composable
fun View3DModelScreen(
    onBackPressed: () -> Unit
) {
    var selectedModel by remember { mutableStateOf(model3DList.first()) } // Default model

    CustomScaffold(
        bottomBarText = "View 3D Model",
        showBackButton = true,
        onBackPressed = onBackPressed
    ) {
        GLBModelViewer(selectedModel, onModelSelected = { model -> selectedModel = model })
    }
}

@Composable
fun GLBModelViewer(selectedModel: Model3D, onModelSelected: (Model3D) -> Unit) {
    val engine = rememberEngine()
    val view = rememberView(engine)
    val renderer = rememberRenderer(engine)
    val scene = rememberScene(engine)
    val modelLoader = rememberModelLoader(engine)
    val materialLoader = rememberMaterialLoader(engine)
    val environmentLoader = rememberEnvironmentLoader(engine)
    val collisionSystem = rememberCollisionSystem(view)

    var showPopup by remember { mutableStateOf(false) }
    var rotationAngle by remember { mutableFloatStateOf(0f) }
    var cameraNode = rememberCameraNode(engine) { position = Position(z = 4.0f) }
    val initialCameraPosition = remember { cameraNode.position }

    var modelPosition by remember { mutableStateOf(Position(0f, 0f, 0f)) }

    // Use rememberNodes to store and update the modelNode
    val modelNodes = rememberNodes {
        add(
            ModelNode(
                modelInstance = modelLoader.createModelInstance(rawResId = selectedModel.modelResourceId),
                scaleToUnits = 1.0f
            ).apply {
                position = modelPosition
            }
        )
    }

    // Update the model when selectedModel changes
    LaunchedEffect(selectedModel) {
        modelNodes.clear() // Remove previous model
        modelNodes.add(
            ModelNode(
                modelInstance = modelLoader.createModelInstance(rawResId = selectedModel.modelResourceId),
                scaleToUnits = 1.0f
            ).apply {
                position = modelPosition
            }
        )
    }

    // Continuous rotation effect
    LaunchedEffect(Unit) {
        while (true) {
            delay(16L) // ~60 FPS
            rotationAngle += 1f
            modelNodes.firstOrNull()?.rotation = Rotation(y = rotationAngle)
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
            mainLightNode = rememberMainLightNode(engine) { intensity = 100_000.0f },
            environment = rememberEnvironment(environmentLoader) {
                environmentLoader.createHDREnvironment(rawResId = R.raw.neutral)!!
            },
            cameraNode = cameraNode,
            cameraManipulator = rememberCameraManipulator(),
            childNodes = modelNodes,  // Use the dynamic list of nodes
            onGestureListener = rememberOnGestureListener(
                onDoubleTapEvent = { _, tappedNode ->
                    if (tappedNode != null) {
                        showPopup = true
                    }
                }
            ),
            onTouchEvent = { _, _ -> false }
        )

        ModelLazyRow(onModelSelected = onModelSelected)

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
                    modelNodes.firstOrNull()?.position = modelPosition
                }
            )

            ButtonWithIcon(
                iconId = R.drawable.reset,
                textId = R.string.reset,
                onClick = {
                    modelNodes.firstOrNull()?.position = Position(0f, 0f, 0f)
                    cameraNode.position = initialCameraPosition
                },
                primaryButton = false
            )
        }

        if (showPopup) {
            ModelInfoPopup(selectedModel, onDismiss = { showPopup = false })
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
fun ModelInfoPopup(model: Model3D, onDismiss: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable(onClick = onDismiss),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.padding(24.dp),
            shape = MaterialTheme.shapes.medium,
            elevation = CardDefaults.cardElevation(8.dp),
            colors = cardColors(containerColor = MaterialTheme.colorScheme.background)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = model.name, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.Bold)

                Spacer(modifier = Modifier.height(8.dp))

                Text(text = model.description, style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = onDismiss) { Text("Close") }
            }
        }
    }
}

@Composable
fun ModelLazyRow(onModelSelected: (Model3D) -> Unit) {
    LazyRow(
        userScrollEnabled = true,
        modifier = Modifier
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(model3DList.size) { index ->
            Button(
                onClick = { onModelSelected(model3DList[index]) },
                colors = buttonColors(
                    containerColor = MaterialTheme.colorScheme.onSecondary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text(model3DList[index].name)
            }
        }
    }
}

val model3DList = listOf(
    Model3D("Rose", "A beautiful red rose preserved in a transparent glass dome, symbolizing love, beauty, and eternity.", R.raw.rose),
    Model3D("Sunflower", "A bright and cheerful sunflower with golden petals and a sturdy green stem, capturing the essence of warmth and vitality.", R.raw.sunflower),
    Model3D("Tulip", "A graceful pair of purple tulips standing together in elegant harmony, symbolizing elegance and admiration.", R.raw.tulip),
    Model3D("Cherry blossom", "A lovely cherry blossom branch adorned with soft pink flowers, capturing the beauty of spring.", R.raw.cherry_blossom_branch),
    Model3D("Lily", "A dreamy bunch of pink lilies, with some in full bloom and others still in bud, radiating natural beauty.", R.raw.lilies),
    Model3D("Lotus", "A serene pink lotus in full bloom, with gracefully layered petals radiating purity and tranquility.", R.raw.lotus),
    Model3D("Orchid", "A graceful branch of purple orchids with elegant, velvety petals cascading in a delicate arc.", R.raw.orchid)
)
