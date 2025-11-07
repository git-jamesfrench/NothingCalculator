package com.jamesfrench.nothingcalculator

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
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

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun App(innerPadding: PaddingValues, modifier: Modifier = Modifier, sharedViewModel: SharedViewModel = SharedViewModel(ResourceProvider(
    LocalContext.current
))) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "home",
        enterTransition = { EnterTransition.None },
        exitTransition =  { ExitTransition.KeepUntilTransitionsFinished },
        popEnterTransition =  { EnterTransition.None },
        popExitTransition = { ExitTransition.KeepUntilTransitionsFinished },
    ) {
        composable(
            "home",
        ) {
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    TopNavigation(
                        onSettingsClick = { navController.navigate("settings") },
                        onHistoryClick = { navController.navigate("history") }
                    )
                    Spacer(modifier = Modifier.weight(0.8f))
                    Calculations(sharedViewModel)
                    Spacer(modifier = Modifier.weight(0.3f))
                    Result(sharedViewModel)
                    Spacer(modifier = Modifier.weight(1f))
                    Keys(sharedViewModel)
                }
            }
        }
        composable(
            "settings",
            enterTransition = { slideInHorizontally(
                initialOffsetX = { fullWidth -> fullWidth },
                animationSpec = tween(200, easing = EaseInOut)
            ) },
            exitTransition = { slideOutHorizontally(
                targetOffsetX = { fullWidth -> fullWidth },
                animationSpec = tween(200, easing = EaseInOut)
            ) },
            popEnterTransition = { slideInHorizontally(
                initialOffsetX = { fullWidth -> fullWidth },
                animationSpec = tween(200, easing = EaseInOut)
            ) },
            popExitTransition = { slideOutHorizontally(
                targetOffsetX = { fullWidth -> fullWidth },
                animationSpec = tween(200, easing = EaseInOut)
            ) },
        ) {
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                Settings(onSettingsClose = { navController.popBackStack() })
            }
        }
        composable(
            "history",
            enterTransition = { slideInHorizontally(
                initialOffsetX = { fullWidth -> -fullWidth },
                animationSpec = tween(200, easing = EaseInOut)
            ) },
            exitTransition = { slideOutHorizontally(
                targetOffsetX = { fullWidth -> -fullWidth },
                animationSpec = tween(200, easing = EaseInOut)
            ) },
            popEnterTransition = { slideInHorizontally(
                initialOffsetX = { fullWidth -> -fullWidth },
                animationSpec = tween(200, easing = EaseInOut)
            ) },
            popExitTransition = { slideOutHorizontally(
                targetOffsetX = { fullWidth -> -fullWidth },
                animationSpec = tween(200, easing = EaseInOut)
            ) },
        ) {
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                History(onHistoryClose = { navController.popBackStack() })
            }
        }
    }
}