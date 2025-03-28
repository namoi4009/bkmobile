package com.example.flowerapp.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = pink_400,
    secondary = green_500,
    tertiary = pink_600,
    background = pink_200,
    surface = green_200,
    onPrimary = pink_600,
    onSecondary = green_300,
    onTertiary = Color.White,
    onBackground = dark_text,
    onSurface = green_700,
    onSecondaryContainer = green_600

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
fun customTextFieldColors(): TextFieldColors {
    return TextFieldDefaults.colors(
        cursorColor = LightColorScheme.onSurface,
        selectionColors = TextSelectionColors(
            handleColor = LightColorScheme.tertiary,
            backgroundColor = LightColorScheme.tertiary
        ),

        unfocusedIndicatorColor = Color.Transparent,
        focusedIndicatorColor = LightColorScheme.onSurface,
        disabledIndicatorColor = Color.Transparent,

        focusedLeadingIconColor = LightColorScheme.secondary,
        unfocusedLeadingIconColor = LightColorScheme.secondary,
        focusedTrailingIconColor = LightColorScheme.secondary,
        unfocusedTrailingIconColor = LightColorScheme.secondary,

        focusedContainerColor = LightColorScheme.surface,
        unfocusedContainerColor = LightColorScheme.surface,
    )
}

@Composable
fun FlowerAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}