package com.jamesfrench.nothingcalculator

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.jamesfrench.nothingcalculator.ui.theme.NothingCalculatorTheme

class MainActivity : ComponentActivity() {
    @SuppressLint("SourceLockedOrientationActivity")
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)

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
            TopNavigation()
            Spacer(modifier = Modifier.weight(0.8f))
            Calculations(sharedViewModel)
            Spacer(modifier = Modifier.weight(0.3f))
            Result(sharedViewModel)
            Spacer(modifier = Modifier.weight(1f))
            Keys(sharedViewModel)
        }
    }
}