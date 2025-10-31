package com.jamesfrench.nothingcalculator.ui.theme

import android.app.Activity
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = DeepWhite,
    secondary = DeepWhite,
    tertiary = DeepWhite,
    background = DeepBlack,
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

@RequiresApi(Build.VERSION_CODES.R)
@Composable
fun NothingCalculatorTheme(content: @Composable () -> Unit) {
    val view = LocalView.current
    val window = (view.context as Activity).window

    SideEffect {
        val insetsController = WindowCompat.getInsetsController(window, view)
        insetsController.isAppearanceLightStatusBars = false
    }

    MaterialTheme(
        colorScheme = DarkColorScheme,
        content = content
    )
}