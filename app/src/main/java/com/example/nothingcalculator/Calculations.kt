package com.example.nothingcalculator

import android.content.ClipData
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.Clipboard
import androidx.compose.ui.platform.InterceptPlatformTextInput
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalTextToolbar
import androidx.compose.ui.platform.TextToolbar
import androidx.compose.ui.platform.TextToolbarStatus
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nothingcalculator.ui.theme.ContrastedGray
import com.example.nothingcalculator.ui.theme.DeepBlack
import com.example.nothingcalculator.ui.theme.DeepWhite
import com.example.nothingcalculator.ui.theme.NothingRed
import com.example.nothingcalculator.ui.theme.ndot77
import com.example.nothingcalculator.ui.theme.notosans
import kotlinx.coroutines.awaitCancellation

@OptIn(ExperimentalComposeUiApi::class)
@Composable fun DisableSoftKeyboard(
    disable: Boolean = true,
    content: @Composable () -> Unit,
) {
    InterceptPlatformTextInput(
        interceptor = { request, nextHandler ->
            if (!disable) {
                nextHandler.startInputMethod(request)
            } else {
                awaitCancellation()
            }
        },
        content = content,
    )
}

object EmptyTextToolbar: TextToolbar {
    override val status: TextToolbarStatus = TextToolbarStatus.Hidden

    override fun hide() {  }

    override fun showMenu(
        rect: Rect,
        onCopyRequested: (() -> Unit)?,
        onPasteRequested: (() -> Unit)?,
        onCutRequested: (() -> Unit)?,
        onSelectAllRequested: (() -> Unit)?,
    ) {
    }
}

@Composable
fun Calculations(viewModel: SharedViewModel) {
    val customSelectionColors = TextSelectionColors(
        handleColor = NothingRed,
        backgroundColor = NothingRed.copy(alpha = 0.4f)
    )
    val focusManager = LocalFocusManager.current

    LaunchedEffect(viewModel.clearFocusRequest) {
        if (viewModel.clearFocusRequest) {
            focusManager.clearFocus()
            viewModel.clearFocusDone()
        }
    }

    Box (
        modifier = Modifier
            .padding(start = 15.dp, end = 15.dp)
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        DisableSoftKeyboard {
            CompositionLocalProvider(LocalTextSelectionColors provides customSelectionColors, LocalTextToolbar provides EmptyTextToolbar) {
                BasicTextField(
                    value = viewModel.textState,
                    onValueChange = {
                        viewModel.textState = it
                        viewModel.checkSelection()
                    },
                    cursorBrush = SolidColor(NothingRed),
                    textStyle = TextStyle(color = DeepWhite, fontSize = 32.sp, fontFamily = notosans, textAlign = TextAlign.End),
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun Result(viewModel: SharedViewModel) {
    val haptic = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed = interactionSource.collectIsPressedAsState().value
    val clipboard: Clipboard = LocalClipboard.current
    val textSize = if(viewModel.result.length < 16) 64.sp else 32.sp

    val color by animateColorAsState(
        targetValue = if (isPressed) ContrastedGray else DeepBlack,
        animationSpec = if (isPressed)
            tween(15, easing = EaseOut)
        else
            tween(400, easing = EaseIn)
    )

    Text(
        text = viewModel.result,
        color = DeepWhite,
        fontSize = textSize,
        fontFamily = ndot77,
        textAlign = TextAlign.End,
        lineHeight = textSize,
        modifier = Modifier
            .padding(start = 15.dp, end = 15.dp)
            .defaultMinSize(minHeight = with(LocalDensity.current) {64.sp.toDp()})
            .fillMaxWidth()
            .drawBehind {
                val strokeWidth = 15.dp.toPx()
                val halfStroke = strokeWidth / 2
                drawRoundRect(
                    color = color,
                    topLeft = Offset(-halfStroke, -halfStroke),
                    size = Size(size.width + strokeWidth, size.height + strokeWidth),
                    cornerRadius = CornerRadius(15.dp.toPx())
                )
            }
            .background(color, RectangleShape)
            .clickable(
                interactionSource = interactionSource,
                enabled = viewModel.result.isNotEmpty(),
                indication = null,
                onClickLabel = null,
                role = null,
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)

                    val clip: ClipData = ClipData.newPlainText("Result", viewModel.result)
                    clipboard.nativeClipboard.setPrimaryClip(clip)
                }
            )
    )
}