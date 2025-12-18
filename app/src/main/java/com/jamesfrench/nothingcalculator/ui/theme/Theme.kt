package com.jamesfrench.nothingcalculator.ui.theme

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class ColorScheme(
    val buttonPrimary: Color,
    val buttonPrimaryPressed: Color,
    val buttonSecondary: Color,

    val background: Color,
    val buttonContainer: Color,

    val textPrimary: Color,
    val iconsPrimary: Color,
)

val LocalAppColors = staticCompositionLocalOf<ColorScheme> {
    error("No AppColors provided")
}

val darkTheme = ColorScheme(
    buttonPrimary = ContrastedGray,
    buttonPrimaryPressed = SqueezedContrastedGray,
    buttonSecondary = DeepWhite,

    background = DeepBlack,
    buttonContainer = ContrastedGray,

    textPrimary = DeepWhite,
    iconsPrimary = DeepWhite,
)

val lightTheme = ColorScheme(
    buttonPrimary = WhiteButton,
    buttonPrimaryPressed = WhiteButtonPressed,
    buttonSecondary = DeepWhite,

    background = DeepWhite,
    buttonContainer = ContrastedGray,

    textPrimary = DeepBlack,
    iconsPrimary = DeepBlack,
)

@RequiresApi(Build.VERSION_CODES.R)
@Composable
fun T(
    content: @Composable () -> Unit
) {

    val colorScheme: ColorScheme = if (isSystemInDarkTheme()) {
        darkTheme
    } else {
        lightTheme
    }

    CompositionLocalProvider(
        LocalAppColors provides colorScheme
    ) {
        content()
    }
}