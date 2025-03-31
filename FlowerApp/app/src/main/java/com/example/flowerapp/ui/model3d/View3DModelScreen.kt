package com.example.flowerapp.ui.model3d

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.flowerapp.R
import io.github.sceneview.Scene
import io.github.sceneview.math.Position
import io.github.sceneview.node.ModelNode
import io.github.sceneview.rememberCameraManipulator
import io.github.sceneview.rememberCameraNode
import io.github.sceneview.rememberEngine
import io.github.sceneview.rememberModelLoader
import io.github.sceneview.rememberNodes
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
    Scene(
        modifier = Modifier.fillMaxSize(),
        engine = engine,
        view = view,
        renderer = renderer,
        scene = scene,
        modelLoader = modelLoader,
        cameraNode = rememberCameraNode(engine) {
            position = Position(z = 4.0f)
        },
        cameraManipulator = rememberCameraManipulator(),
        childNodes = rememberNodes {
            add (
                ModelNode(
                    modelInstance = modelLoader.createModelInstance(rawResId = modelResourceId),
                    scaleToUnits = 1.0f
                )
            )
        }
    )
}