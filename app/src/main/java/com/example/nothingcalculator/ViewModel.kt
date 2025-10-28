package com.example.nothingcalculator

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import net.objecthunter.exp4j.ExpressionBuilder

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
        } else if (addition == "=") {
            if (result.isNotEmpty()) {
                textState = textState.copy(
                    text = result,
                    selection = TextRange(result.length)
                )
            }
        } else {
            // Add character
            var textResult = StringBuilder(textState.text)
            var spaceAdded = 1

            // Replacement if necessary
            if (textState.selection.length != 0) {
                textResult = textResult.replace(start, end, "")
            }

            // Replacement if last character is an operator (except minus)
            if ( !(isExpression(addition) && textState.text.isEmpty()) ) {
                if (isExpression(addition) && isExpression(textState.text[start - 1].toString()) && addition != "–") {
                    textResult = textResult.replace(start - 1, start, addition)
                    spaceAdded = 0
                } else {
                    textResult = textResult.insert(start, addition)
                }
            }

            // Adds "0" if possible
            if (addition == "," && (start == 0 || isExpression(textState.text[start - 1].toString()))) {
                textResult = textResult.insert(start, "0")
                spaceAdded = 2
            }

            textState = textState.copy(
                text = textResult.toString(),
                selection = TextRange(start + spaceAdded)
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
        var cleaned = expression

        // Remove "-" if useless
        if (cleaned.endsWith("–")) {
            cleaned = cleaned.removeSuffix("–")
        }

        // Remove "(" if useless
        if (cleaned.endsWith("(")) {
            cleaned = cleaned.removeSuffix("(")
        }

        // Remove operator if useless
        if (cleaned.isNotEmpty() && isExpression(cleaned.last().toString())) {
            cleaned = cleaned.removeSuffix(cleaned.last().toString())
        }

        // Adds missing brackets if necessary
        val missingBrackets = cleaned.count { it == '(' } - cleaned.count { it == ')' }
        if (missingBrackets > 0) {
            cleaned += ")".repeat(missingBrackets)
        }

        // Replacement of operators
        if (cleaned.isNotEmpty()) {
            cleaned = cleaned
                .replace(",", ".")
                .replace("×", "*")
                .replace("÷", "/")
                .replace("–", "-")
                .replace("e", "E")
        }

        return cleaned
    }

    fun evaluateExpression() {
        try {
            val cleanedExpression = cleanExpression(textState.text)

            if (cleanedExpression.isNotEmpty()) {
                val resultExpression = ExpressionBuilder(cleanedExpression).build().evaluate()

                val resultOfCalculation = resultExpression.toString()
                    .replace(".", ",")
                    .replace("E", "e")
                    .removeSuffix(",0")

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