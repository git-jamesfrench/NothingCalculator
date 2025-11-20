package com.jamesfrench.nothingcalculator

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import net.objecthunter.exp4j.ExpressionBuilder

class ResourceProvider(private val context: Context) {
    fun getString(resId: Int): String {
        return context.getString(resId)
    }
}

val expressions = listOf<Char>('*', '/', '-', '+', '¤')
val numbers = listOf<Char>('1', '2', '3', '4', '5', '6', '7', '8', '9', '0')

class SharedViewModel(private val resourceProvider: ResourceProvider) : ViewModel() {
    var textState by mutableStateOf(TextFieldValue(""))
    var clearFocusRequest by mutableStateOf(false)
        private set

    var isRemoveEnabled by mutableStateOf(value = false)
        private set

    var result by mutableStateOf("")
        private set

    var closingBrackets by mutableStateOf(false)
        private set

    fun keyPressed(addition: String) {
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
            var addition = addition
            var spaceAdded = 1

            // Replacement if selection (Don't remove it!)
            if (textState.selection.length != 0) {
                textResult = textResult.replace(start, end, "")
            }

            textResult = textResult.insert(start, addition) // The most important line

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


    fun evaluateExpression() {
        try {
            val cleanedExpression = textState.text

            if (cleanedExpression.isNotEmpty()) {
                //val resultExpression = ExpressionBuilder(cleanedExpression).build().evaluate()

                //val resultOfCalculation = resultExpression.toString()
                //    .removeSuffix(".0")
                //    .replace(".", resourceProvider.getString(R.string.decimal))
                //    .replace("E", "e")

                result = cleanedExpression
            } else { result = "" }
        } catch (e: ArithmeticException) {
            result = resourceProvider.getString(R.string.division_by_zero)
        } catch (e: IllegalArgumentException) {
            result = resourceProvider.getString(R.string.invalid_format)
        } catch (e: Exception) {
            result = "⚠\n $e"
        }
    }

    fun checkSelection() {
        val missingBrackets = textState.text.count{ it == '(' } - textState.text.count{ it == ')' }

        isRemoveEnabled = textState.selection.start != 0 || textState.selection.length > 0
        closingBrackets = missingBrackets > 0 && textState.text[textState.selection.start - 1] !in expressions
    }
}