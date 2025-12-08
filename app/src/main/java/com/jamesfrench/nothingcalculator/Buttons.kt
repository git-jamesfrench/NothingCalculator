package com.jamesfrench.nothingcalculator

import android.os.Build
import android.os.VibrationEffect
import androidx.annotation.RequiresApi
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
import android.os.Vibrator

data class KeysValue(val symbol: Any, val category: String, val background: Color, val font: FontFamily, val weight: Float, val numberOfPulses: Int, val value: Any = symbol)

private val KeysValues = listOf(
    listOf(
        KeysValue("(", "number", NothingRed, ndot77, 1f, 1, "("),
        KeysValue("%", "suffix",NothingRed, ndot77, 1f, 1),
        KeysValue("÷", "operator",NothingRed, ndot77, 1f, 1, "/"),
        KeysValue("×", "operator",NothingRed, ndot77, 1f, 1, "*")
    ),
    listOf(
        KeysValue("7", "number",ContrastedGray, notosans, 1f, 1),
        KeysValue("8", "number",ContrastedGray, notosans, 1f, 1),
        KeysValue("9", "number",ContrastedGray, notosans, 1f, 1),
        KeysValue("–", "negative",NothingRed, ndot77, 1f, 1, "-")
    ),
    listOf(
        KeysValue("4", "number",ContrastedGray, notosans, 1f, 1),
        KeysValue("5", "number",ContrastedGray, notosans, 1f, 1),
        KeysValue("6", "number",ContrastedGray, notosans, 1f, 1),
        KeysValue("+", "operator",NothingRed, ndot77, 1f, 1)
    ),
    listOf(
        KeysValue("1", "number",ContrastedGray, notosans, 1f, 1),
        KeysValue("2", "number",ContrastedGray, notosans, 1f, 1),
        KeysValue("3", "number",ContrastedGray, notosans, 1f, 1),
        KeysValue(R.string.decimal, "number",NothingRed, ndot77, 1f, 1, ".")
    ),
    listOf(
        KeysValue("0", "number",ContrastedGray, notosans, 2f, 1),
        KeysValue("<", "del",DeepWhite, ndot77, 1f, 1),
        KeysValue("=", "equal",NothingRed, ndot77, 1f, 3)
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

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Key(viewModel: SharedViewModel, text: String, value: String, category: String, background: Color, foreground: Color, font: FontFamily, numberOfPulses: Int, modifier: Modifier = Modifier) {
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

    var hapticEngine by remember { mutableStateOf<VibrationEffect?>(null) }
    val context = LocalContext.current

    // https://medium.com/@jpmtech/haptics-in-jetpack-compose-06ac8adaf985
    // May change the code when i'm a bit more advanced in programming apps
    LaunchedEffect(key1 = Unit) {
        val numberOfPulses = numberOfPulses // Number of increasing haptic pulses
        val pulseDuration = 20L // Duration of each pulse in milliseconds
        val spaceBetweenPulses = 20L // Duration of space between pulses in milliseconds
        val maxAmplitude = 255 // Maximum amplitude for the last pulse

        val timings = LongArray(numberOfPulses * 2) // Double the size for on/off
        val amplitudes = IntArray(numberOfPulses * 2)

        for (i in 0 until numberOfPulses) {
            val amplitude = (maxAmplitude * (i + 1) / numberOfPulses) // Calculate increasing amplitude
            timings[i * 2] = spaceBetweenPulses // Space before the pulse
            timings[i * 2 + 1] = pulseDuration // Duration of the pulse
            amplitudes[i * 2] = 0 // Amplitude of the space
            amplitudes[i * 2 + 1] = amplitude // Amplitude of the pulse
        }

        hapticEngine = VibrationEffect.createWaveform(timings, amplitudes, -1)
    }

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
                enabled = viewModel.enabledCategories[category] == true, // this stupid thing wants me to put "== true" which is completely useless, I WANT TO GO BACK TO PYTHON!!
                onClickLabel = null,
                role = null,
                onClick = {
                    //haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    hapticEngine?.let {
                        val vibrator = context.getSystemService(Vibrator::class.java)
                        vibrator.vibrate(it)
                    }
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
                key.category,
                key.background,
                if (key.background == DeepWhite) DeepBlack else DeepWhite,
                key.font,
                key.numberOfPulses,
                modifier = Modifier
                    .weight(key.weight)
                    .aspectRatio(1f * key.weight)
            )
        }
    }
}