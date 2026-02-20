package com.subs4what.app.ui.components

import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.subs4what.app.ui.theme.Accent

@Composable
fun NothingAnimation(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "breath")

    val alpha by transition.animateFloat(
        initialValue = 0.08f,
        targetValue = 0.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    val scale by transition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Box(
        modifier = modifier.size(160.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(160.dp)) {
            val r = size.minDimension / 2 * scale

            drawCircle(
                color = Accent.copy(alpha = alpha * 0.4f),
                radius = r,
                style = Stroke(width = 0.8.dp.toPx())
            )

            drawCircle(
                color = Accent.copy(alpha = alpha * 0.1f),
                radius = r * 0.5f
            )

            drawCircle(
                color = Accent.copy(alpha = alpha),
                radius = 2.dp.toPx()
            )
        }
    }
}
