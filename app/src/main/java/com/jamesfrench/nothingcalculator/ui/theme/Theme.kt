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
    val buttonSecondaryPressed: Color,

    val background: Color,
    val buttonContainer: Color,

    val textPrimary: Color,
    val textSecondary: Color,
    val iconsPrimary: Color,
    val iconsSecondary: Color,
)

val LocalAppColors = staticCompositionLocalOf<ColorScheme> {
    error("No AppColors provided")
}

val darkTheme = ColorScheme(
    buttonPrimary = ContrastedGray,
    buttonPrimaryPressed = SqueezedContrastedGray,

    buttonSecondary = DeepWhite,
    buttonSecondaryPressed = SqueezedDeepWhite,

    background = DeepBlack,
    buttonContainer = ContrastedGray,

    textPrimary = DeepWhite,
    textSecondary = SqueezedDeepWhite,
    iconsPrimary = DeepWhite,
    iconsSecondary = SqueezedDeepWhite,
)

val lightTheme = ColorScheme(
    buttonPrimary = WhiteButton,
    buttonPrimaryPressed = WhiteButtonPressed,

    buttonSecondary = DeepBlack,
    buttonSecondaryPressed = SqueezedDeepBlack,

    background = DeepWhite,
    buttonContainer = ContrastedGray,

    textPrimary = DeepBlack,
    textSecondary = TooSqueezedDeepBlack,
    iconsPrimary = DeepBlack,
    iconsSecondary = TooSqueezedDeepBlack,
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