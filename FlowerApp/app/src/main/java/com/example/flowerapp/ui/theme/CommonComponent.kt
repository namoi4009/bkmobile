package com.example.flowerapp.ui.theme

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Text
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults.buttonColors
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CustomImage(imageId: Int, size: Dp? = null) {
    Image(
        painterResource(id = imageId),
        contentDescription = "logo",
        modifier = size?.let { Modifier.size(it) } ?: Modifier
    )
}

@Composable
fun CustomScaffold(
    bottomBarText: String, // Text for bottom bar
    showBackButton: Boolean = false, // Flag to show back button
    onBackPressed: () -> Unit = {}, // Back button action
    content: @Composable (PaddingValues) -> Unit // Content after innerPadding
) {
    Scaffold(
        bottomBar = {
            BottomAppBar(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Leftmost Back Button
                    if (showBackButton) {
                        IconButton(onClick = { onBackPressed() }) {
                            Icon(
                                Icons.Default.PlayArrow,
                                contentDescription = "Back",
                                modifier = Modifier.graphicsLayer(scaleX = -1f)
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.width(48.dp)) // Maintain balance if no button
                    }

                    // Centered Text
                    Text(
                        modifier = Modifier.weight(1f), // This makes it take the remaining space evenly
                        textAlign = TextAlign.Center,
                        text = bottomBarText,
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold
                    )

                    // Equal Spacer on the Right
                    Spacer(modifier = Modifier.width(48.dp)) // Balances the layout
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier.padding(innerPadding)
        ) {
            content(innerPadding)
        }
    }
}

@Composable
fun MenuCard(
    imageId: Int,
    text: String,
    onClick: () -> Unit
) {

    Column(
        modifier = Modifier
            .wrapContentSize()
            .padding(30.dp)
            .clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Ensure CustomImage doesn't add extra space
        CustomImage(
            imageId = imageId,
        )

        Box(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.onSurface)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                color = MaterialTheme.colorScheme.onSecondary,
                style = MaterialTheme.typography.headlineLarge
            )
        }
    }
}


@Composable
fun ButtonWithIcon(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    iconId: Int,
    textId: Int? = null,
    primaryButton: Boolean = true
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 36.dp, vertical = 16.dp),
        colors = buttonColors(
            containerColor = if(primaryButton) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
            contentColor = if(primaryButton) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSecondary
        )
    ) {
        Icon(
            painter = painterResource(iconId),
            contentDescription = ""
        )
        Spacer(modifier = Modifier.width(8.dp))
        if (textId != null) {
            Text(
                text = stringResource(id = textId),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        }
    }
}

@Composable
fun CustomFloatingActionButton(
    onClick: () -> Unit,
    primaryButton: Boolean = true,
    iconId: Int,
    modifier: Modifier
) {
    FloatingActionButton(
        onClick = onClick,
        shape = CircleShape,
        containerColor = if(primaryButton) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSecondary,
        contentColor = if(primaryButton) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.secondary,
        modifier = modifier
    ) {
        Icon(
            painterResource(iconId),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(0.7f)
        )
    }
}

@Composable
fun CommonVSpace(heightValue: Dp = 30.dp) {
    Spacer(modifier = Modifier.height(heightValue))
}