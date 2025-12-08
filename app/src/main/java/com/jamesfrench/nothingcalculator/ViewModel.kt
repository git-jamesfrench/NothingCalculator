package com.jamesfrench.nothingcalculator

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import com.ezylang.evalex.BaseException
import com.ezylang.evalex.Expression
import java.math.BigDecimal
import java.math.RoundingMode

class ResourceProvider(private val context: Context) {
    fun getString(resId: Int): String {
        return context.getString(resId)
    }
}

val expressions = listOf<Char>('*', '/', '-', '+')
val numbers = listOf<Char>('1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '%')

class SharedViewModel(private val resourceProvider: ResourceProvider) : ViewModel() {
    var equation by mutableStateOf(TextFieldValue(""))
    var result by mutableStateOf("")
        private set
    var enabledCategories = mutableStateMapOf<String, Boolean>("del" to false, "operator" to true, "suffix" to true, "equal" to false, "number" to true, "negative" to true)
        private set
    var showResult = mutableStateOf(false)
        private set

    var clearFocusRequest by mutableStateOf(false)
        private set
    var isRemoveEnabled by mutableStateOf(value = false)
        private set
    var closingBrackets by mutableStateOf(false)
        private set

    var error: String? = null

    fun changeEquation(text: String, selection: Int) {
        equation = equation.copy(
            text = text,
            selection = TextRange(selection)
        )
        checkAvailableButtons()
        val sequences = equation.text.replace("e+", "").split(regex=Regex("[()*/+-]")).filter { it != "" }
        showResult.value = sequences.size + equation.text.count { it == '%' } > 1
    }

    fun checkAvailableButtons() {
        enabledCategories["del"] = equation.text.isNotEmpty()
        enabledCategories["equal"] = showResult.value // Only work if result is shown, simplifies things
    }

    fun keyPressed(key: String) {
        val start = if (equation.selection.start < equation.selection.end) equation.selection.start else equation.selection.end
        val end = if (equation.selection.start < equation.selection.end) equation.selection.end else equation.selection.start

        if (key == "<") {
            // Remove
            var textResult = StringBuilder(equation.text)
            var cursorPlacement = 0

            if (equation.selection.length == 0) {
                textResult = textResult.deleteAt(start - 1)
                cursorPlacement = -1
            } else {
                textResult = textResult.replace(start, end, "")
            }

            changeEquation(textResult.toString(), start + cursorPlacement)

            evaluateExpression()
        } else if (key == "=") {
            if (error == null) {
                if (result.isNotEmpty()) {
                    changeEquation(result, result.length)
                }
            } else {
                result = error ?: ""
            }
        } else {
            // Add character
            var textResult = StringBuilder(equation.text)
            var addition = key
            var spaceAdded = 1

            // Replacement if selection (Don't remove it!)
            if (equation.selection.length != 0) {
                textResult = textResult.replace(start, end, "")
            }
            if (addition == "(" && closingBrackets) {
                addition = ")"
            }

            textResult = textResult.insert(start, addition) // The most important line

            changeEquation(textResult.toString(), start + spaceAdded)

            evaluateExpression()
        }

        checkSelection()

        // Hide cursor if useless
        if (equation.selection.length == 0 && equation.text.length == equation.selection.start) {
            clearFocusRequest = true
        }
    }

    fun clearFocusDone() {
        clearFocusRequest = false
    }

    fun cleanExpression(operation: String): String {
        val cleaned = StringBuilder(operation)

        // Clean missing characters before and after dots
        var i = 0
        while (i < cleaned.length) {
            if (cleaned[i] == '.' && cleaned.getOrElse(i - 1) {'¤'} !in numbers) {
                cleaned.insert(i, "0")
                i += 1
            } else if (cleaned[i] == '.' && cleaned.getOrElse(i + 1) {'¤'} !in numbers) {
                cleaned.insert(i + 1, "0")
            }
            i += 1
        }

        // Clean operators
        i = 0
        while (i < cleaned.length) {
            if (cleaned[i] in expressions && !(cleaned[i] == '-' && cleaned.getOrElse(i + 1) {'¤'} in numbers + '(')) {
                if (cleaned.getOrElse(i - 1) {'¤'} !in numbers + ')') {
                    cleaned.deleteCharAt(i)
                } else if (cleaned.getOrElse(i + 1) {'¤'} !in numbers + '(' + (if (cleaned.getOrElse(i + 2) {'¤'} in numbers + '(') '-' else null)) {
                    cleaned.deleteCharAt(i)
                } else {
                    i += 1
                }
            } else {
                i += 1
            }
        }

        // Close unclosed parentheses
        repeat(cleaned.count {it == '('} - cleaned.count {it == ')'}) {cleaned.append(')')}

        return cleaned.toString()
    }

    fun evaluateExpression() {
        try {
            val cleanedExpression = cleanExpression(equation.text)

            if (cleanedExpression.isNotEmpty()) {
                val expression = Expression(cleanedExpression.replace("%", "p"))
                    .with("p", 0.01) // Yes, this is degusting...
                    .evaluate()
                    .numberValue
                val resultExpression = expression
                    .setScale(expression.scale().coerceAtMost(15), RoundingMode.HALF_UP)
                    .let {
                        if (it.abs() > BigDecimal(10_000_000_000)) {
                            it.toEngineeringString()
                        } else {
                            it.toPlainString()
                        }
                    }

                val resultOfCalculation = resultExpression
                    .removeSuffix(".0")
                    .replace("E", "e")

                error = null
                result = resultOfCalculation

            } else { result = "" }
        } catch (e: BaseException) {
            when (e.message) {
                "Division by zero" -> {result = ""; error = resourceProvider.getString(R.string.division_by_zero)}
                "Missing second operand for operator" -> {result = ""; error = resourceProvider.getString(R.string.invalid_format)}
                else -> {result = ""; error = "⚠\n $e"}
            }
        } catch (e: Exception) {
            result = ""
            error = "⚠\n $e"
        }
    }

    fun checkSelection() {
        checkAvailableButtons()
        val missingBrackets = equation.text.count{ it == '(' } - equation.text.count{ it == ')' }

        isRemoveEnabled = equation.selection.start != 0 || equation.selection.length > 0
        closingBrackets = missingBrackets > 0 && equation.text[equation.selection.start - 1] !in expressions + '('
    }
}