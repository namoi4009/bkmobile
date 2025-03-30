package com.example.flowerapp.ui.camera.capturepicture

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults.buttonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.flowerapp.R
import com.example.flowerapp.ui.theme.ButtonWithIcon

val stickers = listOf(
    Sticker(name = "Rose", imageId = R.drawable.s_1_rose),
    Sticker(name = "Tulip", imageId = R.drawable.s_2_tulip),
    Sticker(name = "Sunflower", imageId = R.drawable.s_3_sunflower),
    Sticker(name = "Lily", imageId = R.drawable.s_4_lily),
    Sticker(name = "Daisy", imageId = R.drawable.s_5_daisy),
    Sticker(name = "Orchid", imageId = R.drawable.s_6_orchid),
    Sticker(name = "Marigold", imageId = R.drawable.s_7_marigold),
    Sticker(name = "Daffodil", imageId = R.drawable.s_8_daffodil),
    Sticker(name = "Peony", imageId = R.drawable.s_9_peony),
    Sticker(name = "Cherry Blossom", imageId = R.drawable.s_10_cherry_blossom),
    Sticker(name = "Lavender", imageId = R.drawable.s_11_lavender),
    Sticker(name = "Jasmine", imageId = R.drawable.s_12_jasmine),
    Sticker(name = "Iris", imageId = R.drawable.s_13_iris),
    Sticker(name = "Lotus", imageId = R.drawable.s_14_lotus),
    Sticker(name = "Chrysanthemum", imageId = R.drawable.s_15_chrysanthemum),
    Sticker(name = "Hibiscus", imageId = R.drawable.s_16_hibiscus),
    Sticker(name = "Camellia", imageId = R.drawable.s_17_camellia),
    Sticker(name = "Rhododendron", imageId = R.drawable.s_18_rhododendron),
    Sticker(name = "Gladiolus", imageId = R.drawable.s_19_gladiolus),
    Sticker(name = "Poppy", imageId = R.drawable.s_20_poppy),
)

@Composable
fun StickerCanvas(
    stickers: MutableList<Sticker>,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    val screenSize = LocalConfiguration.current.screenWidthDp to LocalConfiguration.current.screenHeightDp

    Box(modifier = modifier.fillMaxSize()) {
        stickers.forEach { sticker ->
            if (sticker.x == 0f && sticker.y == 0f) {
                sticker.x = with(density) { (screenSize.first.dp.toPx() / 2) - 50 }
                sticker.y = with(density) { (screenSize.second.dp.toPx() / 2) - 50 }
            }

            var position by remember { mutableStateOf(Offset(sticker.x, sticker.y)) }
            var scale by remember { mutableFloatStateOf(sticker.scale) }
            var isResizing by remember { mutableStateOf(false) }

            Box(
                modifier = Modifier
                    .offset { IntOffset(position.x.toInt(), position.y.toInt()) }
                    .pointerInput(Unit) {
                        detectTransformGestures { _, pan, _, _ ->
                            if (!isResizing) {
                                position = Offset(position.x + pan.x, position.y + pan.y)
                            }
                        }
                    }
            ) {
                Box {
                    Image(
                        painter = painterResource(id = sticker.imageId),
                        contentDescription = sticker.name,
                        modifier = Modifier.size((100 * scale).dp)
                    )

                    // Delete Button (Top-left)
                    Button(
                        onClick = { stickers.remove(sticker) },
                        modifier = Modifier
                            .offset((-20).dp, (-20).dp)
                            .size(24.dp)
                    ) {
                        Text("X")
                    }

                    // Resize Handle (Bottom-right)
                    Box(
                        modifier = Modifier
                            .offset((90 * scale).dp, (90 * scale).dp)
                            .size(24.dp)
                            .pointerInput(Unit) {
                                detectTransformGestures { _, pan, _, _ ->
                                    isResizing = true
                                    scale = (scale + pan.x * 0.01f).coerceIn(0.5f, 3f)
                                }
                            }
                    ) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .background(Color.Gray, shape = CircleShape)
                        )
                    }
                }
            }

            // Update sticker properties
            sticker.x = position.x
            sticker.y = position.y
            sticker.scale = scale
        }
    }
}


@Composable
fun ToggleLazyColumnScreen(
    onStickerSelected: (Sticker) -> Unit
) {
    var isListVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            ButtonWithIcon(
                onClick = { isListVisible = !isListVisible },
                iconId = R.drawable.lists
            )

            if (isListVisible) {
                LazyColumn {
                    items(stickers.size) { index ->
                        Button(
                            onClick = { onStickerSelected(stickers[index].copy()) }, // Copy to allow multiple instances
                            colors = buttonColors(
                                containerColor = MaterialTheme.colorScheme.onSecondary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            )
                        ) {
                            Text(stickers[index].name)
                        }
                    }
                }
            }
        }
    }
}