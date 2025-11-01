package com.jamesfrench.nothingcalculator

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jamesfrench.nothingcalculator.ui.theme.ContrastedGray
import com.jamesfrench.nothingcalculator.ui.theme.DeepBlack
import com.jamesfrench.nothingcalculator.ui.theme.DeepWhite
import com.jamesfrench.nothingcalculator.ui.theme.NothingRed
import com.jamesfrench.nothingcalculator.ui.theme.SqueezedContrastedGray
import com.jamesfrench.nothingcalculator.ui.theme.SqueezedDeepWhite
import com.jamesfrench.nothingcalculator.ui.theme.SqueezedNothingRed
import com.jamesfrench.nothingcalculator.ui.theme.ndot77
import com.jamesfrench.nothingcalculator.ui.theme.notosans

data class KeysValue(val symbol: Any, val background: Color, val font: FontFamily, val weight: Float, val value: Any = symbol)

private val KeysValues = listOf(
    listOf(
        KeysValue("(", NothingRed, ndot77, 1f, "("),
        KeysValue("%", NothingRed, ndot77, 1f),
        KeysValue("÷", NothingRed, ndot77, 1f, "/"),
        KeysValue("×", NothingRed, ndot77, 1f, "*")
    ),
    listOf(
        KeysValue("7", ContrastedGray, notosans, 1f),
        KeysValue("8", ContrastedGray, notosans, 1f),
        KeysValue("9", ContrastedGray, notosans, 1f),
        KeysValue("–", NothingRed, ndot77, 1f, "-")
    ),
    listOf(
        KeysValue("4", ContrastedGray, notosans, 1f),
        KeysValue("5", ContrastedGray, notosans, 1f),
        KeysValue("6", ContrastedGray, notosans, 1f),
        KeysValue("+", NothingRed, ndot77, 1f)
    ),
    listOf(
        KeysValue("1", ContrastedGray, notosans, 1f),
        KeysValue("2", ContrastedGray, notosans, 1f),
        KeysValue("3", ContrastedGray, notosans, 1f),
        KeysValue(R.string.decimal, NothingRed, ndot77, 1f, ".")
    ),
    listOf(
        KeysValue("0", ContrastedGray, notosans, 2f),
        KeysValue("<", DeepWhite, ndot77, 1f),
        KeysValue("=", NothingRed, ndot77, 1f)
    )
)

fun squeezedColor(color: Color): Color {
    var squeezedColor: Color = NothingRed

    when (color) {
        NothingRed -> squeezedColor = SqueezedNothingRed
        ContrastedGray -> squeezedColor = SqueezedContrastedGray
        DeepWhite -> squeezedColor = SqueezedDeepWhite
    }
    return squeezedColor
}

@Composable
fun Key(viewModel: SharedViewModel, text: String, value: String, background: Color, foreground: Color, font: FontFamily, modifier: Modifier = Modifier) {
    val haptic = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed = interactionSource.collectIsPressedAsState().value
    val character = if (text == "(") {
        if (viewModel.closingBrackets) ")" else "("
    } else text

    val border by animateFloatAsState(
        targetValue = if (isPressed) 7f else 0f,
        animationSpec = if (isPressed)
            tween(25, easing = EaseOut)
        else
            tween(150, easing = EaseIn)
    )
    val color by animateColorAsState(
        targetValue = if (isPressed) squeezedColor(background) else background,
        animationSpec = if (isPressed)
            tween(25, easing = EaseOut)
        else
            tween(150, easing = EaseIn)
    )

    Box(
        modifier = modifier
            .drawBehind {
                val strokeWidth = border.dp.toPx()
                val halfStroke = strokeWidth / 2
                drawRoundRect(
                    color = color,
                    topLeft = Offset(-halfStroke, -halfStroke),
                    size = Size(size.width + strokeWidth, size.height + strokeWidth),
                    cornerRadius = CornerRadius(100.dp.toPx())
                )
            }
            .background(color = color, RoundedCornerShape(100.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = if (text == "<") viewModel.isRemoveEnabled else true,
                onClickLabel = null,
                role = null,
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    viewModel.keyPressed(value)
                }
            ),
    ) {
        Text(
            text = character,
            color = foreground,
            fontFamily = font,
            fontSize = 32.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxHeight()
                .aspectRatio(1f)
                .wrapContentHeight(align = Alignment.CenterVertically)
                .then(
                    if (font == notosans) Modifier.padding(bottom = 3.dp) else Modifier.padding(
                        top = 4.dp
                    )
                ),
        )
    }
}

@Composable
fun Keys(viewModel: SharedViewModel) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 15.dp, end = 15.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(9.dp)
        ) {
            for (rows in KeysValues.indices) {
                KeysRows(viewModel, rows)
            }
        }
    }
}

@Composable
fun KeysRows(viewModel: SharedViewModel, number: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(9.dp)
    ) {
        for (key in KeysValues[number]) {
            Key(
                viewModel,
                key.symbol as? String ?: stringResource(key.symbol as Int),
                key.value as String,
                key.background,
                if (key.background == DeepWhite) DeepBlack else DeepWhite,
                key.font,
                modifier = Modifier
                    .weight(key.weight)
                    .aspectRatio(1f * key.weight)
            )
        }
    }
}