package com.jamesfrench.nothingcalculator

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jamesfrench.nothingcalculator.ui.theme.NotSoContrastedGray
import com.jamesfrench.nothingcalculator.ui.theme.ndot77

@Composable
fun Settings(onSettingsClose: () -> Unit) {
    val context = LocalContext.current
    val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
    val appVersion = packageInfo.versionName
    val uriHandler = LocalUriHandler.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(T.colors.background)
    )

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 15.dp, end = 15.dp, top = 15.dp, bottom = 15.dp),
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
                    painter = painterResource(R.drawable.x),
                    contentDescription = stringResource(R.string.back),
                    tint = T.colors.iconsPrimary
                )
            }
            Text(
                text = stringResource(R.string.settings),
                fontFamily = ndot77,
                fontSize = 26.sp,
                modifier = Modifier.align(Alignment.Center),
                color = T.colors.textPrimary
            )
        }
        Column (
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(start = 15.dp, end = 15.dp),
            verticalArrangement = Arrangement.spacedBy(15.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column (
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(NotSoContrastedGray)
            ) {
                Setting(stringResource(R.string.github_page), R.drawable.github, null, {
                    uriHandler.openUri("https://github.com/git-jamesfrench/NothingCalculator/")
                }, true)
                Setting(stringResource(R.string.license), R.drawable.scale, stringResource(R.string.gpl3), {
                    uriHandler.openUri("https://www.gnu.org/licenses/gpl-3.0.html#license-text")
                }, true)
                Setting(stringResource(R.string.version), R.drawable.hash, appVersion.toString(), {})
            }
        }
    }
}

@Composable
fun Setting(
    title: String,
    icon: Int,
    value: String?,
    onClick: () -> Unit,
    openIcon: Boolean = false,
){
    var isPressed by remember { mutableStateOf(false) }

    val color by animateColorAsState(
        targetValue = if (isPressed) T.colors.buttonPrimaryPressed else T.colors.buttonPrimary,
        animationSpec = if (isPressed)
            tween(25, easing = EaseOut)
        else
            tween(150, easing = EaseIn)
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true // Thanks to kotlin for a warning I won't be able to remove, f"ck you.
                        tryAwaitRelease()
                        isPressed = false
                    },
                    onTap = {
                        onClick()
                    }
                )
            }
            .padding(15.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(15.dp)
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = title,
            tint = T.colors.iconsPrimary
        )
        Text(
            title,
            fontWeight = FontWeight(500),
            fontSize = 15.sp,
            color = T.colors.textPrimary
        )
        Spacer(modifier = Modifier.weight(1f))
        if (!value.isNullOrEmpty()) {
            Text(
                value,
                fontWeight = FontWeight(500),
                fontSize = 15.sp,
                color = T.colors.textSecondary
            )
        }
        if (openIcon) {
            Icon(
                painter = painterResource(R.drawable.open_link),
                contentDescription = stringResource(R.string.open),
                tint = T.colors.iconsSecondary
            )
        }
    }
}