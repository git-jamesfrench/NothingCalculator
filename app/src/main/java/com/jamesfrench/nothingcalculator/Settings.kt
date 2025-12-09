package com.jamesfrench.nothingcalculator

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.EaseOut
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jamesfrench.nothingcalculator.ui.theme.DeepBlack
import com.jamesfrench.nothingcalculator.ui.theme.NotSoContrastedGray
import com.jamesfrench.nothingcalculator.ui.theme.SqueezedDeepWhite
import com.jamesfrench.nothingcalculator.ui.theme.ndot77
import com.jamesfrench.nothingcalculator.ui.theme.normalText

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
            modifier = Modifier
                .fillMaxSize()
                .padding(15.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column (
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(NotSoContrastedGray)
            ) {
                Setting(R.string.theme, R.drawable.theme, {}) {

                }
                Setting(R.string.language, R.drawable.global, {}) {}
            }
        }
    }
}

@Composable
fun Setting(
    title: Int,
    icon: Int,
    onClick: () -> Unit,
    content: @Composable () -> Unit)
{
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed = interactionSource.collectIsPressedAsState().value

    val color by animateColorAsState(
        targetValue = if (isPressed) squeezedColor(NotSoContrastedGray) else NotSoContrastedGray,
        animationSpec = if (isPressed)
            tween(25, easing = EaseOut)
        else
            tween(150, easing = EaseIn)
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color)
            .clickable(enabled = true, interactionSource = interactionSource, indication = null, onClick = {onClick()})
            .padding(15.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(15.dp)
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = stringResource(title),
        )
        Text(
            stringResource(title),
            fontWeight = FontWeight(500),
            fontSize = 15.sp
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            stringResource(title),
            fontWeight = FontWeight(500),
            fontSize = 15.sp,
            color = SqueezedDeepWhite
        )
    }
}