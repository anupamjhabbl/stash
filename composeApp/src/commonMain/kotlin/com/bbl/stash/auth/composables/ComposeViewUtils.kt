package com.bbl.stash.auth.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

object ComposeViewUtils {
    @Composable
    fun Loading(
        modifier: Modifier = Modifier,
        color: Color = MaterialTheme.colorScheme.tertiaryContainer,
        strokeWidth: Dp = 3.dp
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                modifier = modifier,
                color = color,
                strokeWidth = strokeWidth
            )
        }
    }
}