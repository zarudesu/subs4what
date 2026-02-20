package com.subs4what.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.subs4what.app.ui.components.NothingAnimation
import com.subs4what.app.ui.theme.Accent
import com.subs4what.app.ui.theme.TextMuted
import com.subs4what.app.ui.theme.TextSecondary

@Composable
fun NothingScreen(
    memberNumber: Int,
    onViewMemberCard: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(36.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        NothingAnimation()

        Spacer(modifier = Modifier.height(48.dp))

        Text(
            text = "Thank you.",
            style = MaterialTheme.typography.headlineMedium,
            color = Accent
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Nothing happened. Nothing will happen.\nYour \$1 is making the internet a little better.",
            style = MaterialTheme.typography.bodyLarge,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(28.dp))

        if (memberNumber > 0) {
            Text(
                text = "You are supporter #$memberNumber",
                style = MaterialTheme.typography.titleMedium,
                color = Accent.copy(alpha = 0.7f)
            )
        }

        Spacer(modifier = Modifier.height(48.dp))

        TextButton(onClick = onViewMemberCard) {
            Text(
                text = "View your card",
                style = MaterialTheme.typography.labelLarge,
                color = Accent.copy(alpha = 0.6f)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "This space is intentionally empty.\nEnjoy the silence.",
            style = MaterialTheme.typography.bodySmall,
            color = TextMuted.copy(alpha = 0.3f),
            textAlign = TextAlign.Center
        )
    }
}
