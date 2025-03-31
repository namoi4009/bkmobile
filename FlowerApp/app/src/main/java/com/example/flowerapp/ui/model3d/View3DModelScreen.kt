package com.example.flowerapp.ui.model3d

import android.view.MotionEvent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.flowerapp.R
import io.github.sceneview.Scene
import io.github.sceneview.collision.HitResult
import io.github.sceneview.math.Position
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

@Composable
fun View3DModelScreen() {
    GLBModelViewer(modelResourceId = R.raw.rose)
}

@Composable
fun GLBModelViewer(
    modelResourceId: Int,
) {
    val engine = rememberEngine()
    val view = rememberView(engine)
    val renderer = rememberRenderer(engine)
    val scene = rememberScene(engine)
    val modelLoader = rememberModelLoader(engine)
    val materialLoader = rememberMaterialLoader(engine)
    val environmentLoader = rememberEnvironmentLoader(engine)
    val collisionSystem = rememberCollisionSystem(view)

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
        // Controls whether the render target (SurfaceView) is opaque or not.
        isOpaque = true,
        // Always add a direct light source since it is required for shadowing.
        // We highly recommend adding an [IndirectLight] as well.
        mainLightNode = rememberMainLightNode(engine) {
            intensity = 100_000.0f
        },
        environment = rememberEnvironment(environmentLoader) {
            environmentLoader.createHDREnvironment(
                rawResId = R.raw.neutral
            )!!
        },
        cameraNode = rememberCameraNode(engine) {
            position = Position(z = 4.0f)
        },
        cameraManipulator = rememberCameraManipulator(),
        // Scene nodes
        childNodes = rememberNodes {
            // Add a glTF model
            add(
                ModelNode(
                    // Load it from a binary .glb in the asset files
                    modelInstance = modelLoader.createModelInstance(
                        rawResId = modelResourceId
                    ),
                    scaleToUnits = 1.0f
                )
            )
        },
        onGestureListener = rememberOnGestureListener(
            onDoubleTapEvent = { event, tapedNode ->
                tapedNode?.let { it.scale *= 2.0f }
            }),
        // Receive basics on touch event on the view
        onTouchEvent = { event: MotionEvent, hitResult: HitResult? ->
            hitResult?.let { println("World tapped : ${it.worldPosition}") }
            false
        },
    )
}