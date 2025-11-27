package com.jamesfrench.nothingcalculator

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jamesfrench.nothingcalculator.ui.theme.DeepBlack
import com.jamesfrench.nothingcalculator.ui.theme.ndot77

@Composable
fun Settings(onSettingsClose: () -> Unit) {
    val context = LocalContext.current
    val packageManager = context.packageName

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepBlack)
    )

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 15.dp, end = 15.dp, top = 15.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            IconButton(
                onClick = { onSettingsClose() },
                modifier = Modifier
                    .size(42.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize()
                )
                Icon(
                    painter = painterResource(R.drawable.back),
                    contentDescription = stringResource(R.string.back)
                )
            }
            Text(
                text = stringResource(R.string.settings),
                fontFamily = ndot77,
                fontSize = 26.sp,
                modifier = Modifier.align(Alignment.Center)
            )
        }
        Column (
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Settings are in development!")
            Text(packageManager)
        }
    }
}