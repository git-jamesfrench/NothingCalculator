package com.jamesfrench.nothingcalculator

import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.jamesfrench.nothingcalculator.ui.theme.*

@Composable
fun TopNavigation() {
    var expanded by remember { mutableStateOf(false) }
    val uriHandler = LocalUriHandler.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 15.dp, end = 15.dp, top = 15.dp)
    ) {
        IconButton(
        onClick = {  },
        modifier = Modifier
            .size(42.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize()
            )
            Icon(
                painter = painterResource(R.drawable.history),
                contentDescription = stringResource(R.string.history)
            )
        }
        Spacer(
            modifier = Modifier.weight(1f)
        )
        Box(
            modifier = Modifier
        ) {
            IconButton(
                onClick = { expanded = !expanded },
                modifier = Modifier
                    .size(42.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize()
                )
                Icon(
                    painter = painterResource(R.drawable.ellipsis_vertical),
                    contentDescription = stringResource(R.string.menu)
                )
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                offset = DpOffset(0.dp, 6.dp),
                shape = RoundedCornerShape(25.dp),
                containerColor = ContrastedGray,
            ) {
                DropdownMenuItem(
                    text = { Text(text = stringResource(R.string.settings), style = normalText) },
                    leadingIcon = { Icon(painterResource(R.drawable.cog), contentDescription = stringResource(R.string.settings)) },
                    contentPadding = PaddingValues(start = 20.dp, end = 22.dp),
                    onClick = {

                    }
                )
                DropdownMenuItem(
                    text = { Text(text = stringResource(R.string.source_code_github), style = normalText) },
                    leadingIcon = { Icon(painterResource(R.drawable.github), contentDescription = stringResource(R.string.source_code_github)) },
                    contentPadding = PaddingValues(start = 20.dp, end = 22.dp),
                    onClick = {
                        uriHandler.openUri("https://github.com/git-jamesfrench/NothingCalculator")
                    }
                )
            }
        }
    }
}