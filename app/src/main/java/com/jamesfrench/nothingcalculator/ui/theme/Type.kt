package com.jamesfrench.nothingcalculator.ui.theme

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.jamesfrench.nothingcalculator.R

val ndot77 = FontFamily(Font(R.font.ndot77jpextended))
val notosans = FontFamily(Font(R.font.notosans))
val inter = FontFamily(Font(R.font.inter))

val normalText = TextStyle(
    fontFamily = inter,
    fontWeight = FontWeight.Normal,
    fontSize = 16.sp,
    lineHeight = 20.sp,
    letterSpacing = 0.4.sp
)