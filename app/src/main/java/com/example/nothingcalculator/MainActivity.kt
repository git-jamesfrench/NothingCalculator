package com.example.nothingcalculator

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.pm.ActivityInfo
import androidx.compose.ui.text.font.FontFamily
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.Clipboard
import androidx.compose.ui.platform.InterceptPlatformTextInput
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import com.example.nothingcalculator.ui.theme.*
import com.example.nothingcalculator.ui.theme.NothingCalculatorTheme
import kotlinx.coroutines.awaitCancellation
import net.objecthunter.exp4j.ExpressionBuilder

data class KeysValue(val symbol: String, val background: Color, val font: FontFamily, val weight: Float)

private val KeysValues = listOf(
    listOf(
        KeysValue("(", NothingRed, ndot77, 1f),
        KeysValue("%", NothingRed, ndot77, 1f),
        KeysValue("÷", NothingRed, ndot77, 1f),
        KeysValue("×", NothingRed, ndot77, 1f)
    ),
    listOf(
        KeysValue("7", ContrastedGray, notosans, 1f),
        KeysValue("8", ContrastedGray, notosans, 1f),
        KeysValue("9", ContrastedGray, notosans, 1f),
        KeysValue("–", NothingRed, ndot77, 1f)
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
        KeysValue(",", NothingRed, ndot77, 1f)
    ),
    listOf(
        KeysValue("0", ContrastedGray, notosans, 2f),
        KeysValue("<", DeepWhite, ndot77, 1f),
        KeysValue("=", NothingRed, ndot77, 1f)
    )
)

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

fun SqueezedColor(color: Color): Color {
    var squeezedColor: Color = NothingRed

    when (color) {
        NothingRed -> squeezedColor = SqueezedNothingRed
        ContrastedGray -> squeezedColor = SqueezedContrastedGray
        DeepWhite -> squeezedColor = SqueezedDeepWhite
    }
    return squeezedColor
}

class SharedViewModel : ViewModel() {
    var textState by mutableStateOf(TextFieldValue(""))
    var clearFocusRequest by mutableStateOf(false)
        private set

    var isRemoveEnabled by mutableStateOf(value = false)
        private set

    var result by mutableStateOf("")
        private set

    var closingBrackets by mutableStateOf(false)
        private set

    fun addText(addition: String) {
        val start = if (textState.selection.start < textState.selection.end) textState.selection.start else textState.selection.end
        val end = if (textState.selection.start < textState.selection.end) textState.selection.end else textState.selection.start

        if (addition == "<") {
            // Remove
            var textResult = StringBuilder(textState.text)
            var cursorPlacement = 0

            if (textState.selection.length == 0) {
                textResult = textResult.deleteAt(start - 1)
                cursorPlacement = -1
            } else {
                textResult = textResult.replace(start, end, "")
            }

            textState = textState.copy(
                text = textResult.toString(),
                selection = TextRange(start + cursorPlacement)
            )
        } else {
            // Add character
            var textResult = StringBuilder(textState.text)
            if (textState.selection.length != 0) {
                textResult = textResult.replace(start, end, "")
            }

            if ( !(isExpression(addition) && textState.text.isEmpty()) ) {
                if (isExpression(addition) && isExpression(textState.text[start - 1].toString()) && addition != "–") {
                    textResult = textResult.replace(start - 1, start, addition)
                } else {
                    textResult = textResult.insert(start, addition)
                }
            }

            textState = textState.copy(
                text = textResult.toString(),
                selection = TextRange(start + 1)
            )
        }

        checkSelection()

        // Hide cursor if useless
        if (textState.selection.length == 0 && textState.text.length == textState.selection.start) {
            clearFocusRequest = true
        }

        // Evaluate
        evaluateExpression()
    }

    fun clearFocusDone() {
        clearFocusRequest = false
    }

    fun cleanExpression(expression: String): String {
        var expressionCleaned = expression

        if (expressionCleaned.endsWith("(")) {
            expressionCleaned = expressionCleaned.removeSuffix("(")
        }

        val missingBrackets = expressionCleaned.count { it == '(' } - expressionCleaned.count { it == ')' }

        if (expressionCleaned.isNotEmpty()) {
            if (isExpression(expressionCleaned.last().toString())) {
                val characterToRemove = expressionCleaned.last().toString()
                expressionCleaned = expressionCleaned.removeSuffix(characterToRemove)
            }
        }

        for (i in 0..<missingBrackets) {
            expressionCleaned = "$expressionCleaned)"
        }

        if (expressionCleaned.isNotEmpty()) {
            return expressionCleaned
                .replace(",", ".")
                .replace("×", "*")
                .replace("÷", "/")
                .replace("–", "-")
        } else return ""
    }

    fun evaluateExpression() {
        try {
            val cleanedExpression = cleanExpression(textState.text)

            if (cleanedExpression.isNotEmpty()) {
                val resultExpression = ExpressionBuilder(cleanedExpression).build().evaluate()

                val resultOfCalculation = resultExpression.toString().replace(".", ",").removeSuffix(",0")

                if (resultOfCalculation != cleanedExpression) {
                    result = resultOfCalculation
                } else { result = "" }
            } else { result = "" }
        } catch (e: ArithmeticException) {
            result = "⚠ Division par zéro"
        } catch (e: IllegalArgumentException) {
            result = "⚠ Format invalid"
        } catch (e: Exception) {
            result = "⚠\n $e"
        }
    }

    fun checkSelection() {
        val missingBrackets = textState.text.count{ it == '(' } - textState.text.count{ it == ')' }

        isRemoveEnabled = textState.selection.start != 0 || textState.selection.length > 0
        closingBrackets = missingBrackets > 0 && !isExpression(textState.text[textState.selection.start - 1].toString())
    }

    fun isExpression(char: String): Boolean {
        return char in listOf<String>("×", "÷", "–", "+")
    }
}

class MainActivity : ComponentActivity() {
    @SuppressLint("SourceLockedOrientationActivity")
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); // Fixed portrait orientation

        setContent {
            NothingCalculatorTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    App(innerPadding)
                }
            }
        }
    }
}

@Composable
fun App(innerPadding: PaddingValues, modifier: Modifier = Modifier, sharedViewModel: SharedViewModel = SharedViewModel()) {
    Box (modifier = modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Spacer(modifier = Modifier.weight(1f))
            Calculations(sharedViewModel)
            Spacer(modifier = Modifier.weight(0.3f))
            Result(sharedViewModel)
            Spacer(modifier = Modifier.weight(1f))
            Keys(sharedViewModel)
        }
    }
}

fun keyPress(state: String, key: String): String {
    return state + key
}

@Composable
fun Key(viewModel: SharedViewModel, text: String, background: Color, foreground: Color, font: FontFamily, modifier: Modifier = Modifier) {
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
        targetValue = if (isPressed) SqueezedColor(background) else background,
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
                    viewModel.addText(character)
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
fun Keys(viewModel: SharedViewModel, modifier: Modifier = Modifier) {
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
                KeysRows(viewModel, modifier = Modifier, rows)
            }
        }
    }
}

@Composable
fun KeysRows(viewModel: SharedViewModel, modifier: Modifier, number: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(9.dp)
    ) {
        for (key in KeysValues[number]) {
            Key(
                viewModel,
                key.symbol,
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

@Composable
fun Calculations(viewModel: SharedViewModel, modifier: Modifier = Modifier) {
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
            CompositionLocalProvider(LocalTextSelectionColors provides customSelectionColors) {
                BasicTextField(
                    value = viewModel.textState,
                    onValueChange = {
                        viewModel.textState = it
                        viewModel.checkSelection()
                    },
                    cursorBrush = SolidColor(NothingRed),
                    textStyle = TextStyle(color = DeepWhite, fontSize = 32.sp, fontFamily = notosans, textAlign = TextAlign.End),
                    modifier = Modifier.fillMaxWidth()
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
                enabled = true,
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