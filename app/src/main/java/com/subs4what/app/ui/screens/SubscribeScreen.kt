package com.subs4what.app.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.subs4what.app.billing.SubscriptionState
import com.subs4what.app.ui.components.GoldButton
import com.subs4what.app.ui.theme.Accent
import com.subs4what.app.ui.theme.TextMuted
import com.subs4what.app.ui.theme.TextSecondary

@Composable
fun SubscribeScreen(
    subscriptionState: SubscriptionState,
    priceText: String,
    onSubscribe: () -> Unit,
    onRestore: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 36.dp, vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Subscription\nfor nothing",
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.Bold
            ),
            color = Accent,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = priceText.ifEmpty { "$1.00" } + " / month",
            style = MaterialTheme.typography.titleLarge,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(36.dp))

        Text(
            text = "What you get:",
            style = MaterialTheme.typography.titleSmall,
            color = TextMuted
        )

        Spacer(modifier = Modifier.height(16.dp))

        val points = listOf(
            "Nothing. The app stays empty.",
            "A unique supporter number.",
            "The knowledge that you\u2019re helping keep\nthe internet free and open.",
            "No ads. No tracking. No data collection.",
            "A small act of trust in the digital commons."
        )

        points.forEach { point ->
            PointRow(point)
            Spacer(modifier = Modifier.height(14.dp))
        }

        Spacer(modifier = Modifier.height(36.dp))

        when (subscriptionState) {
            SubscriptionState.Loading, SubscriptionState.Subscribing -> {
                CircularProgressIndicator(
                    modifier = Modifier.size(40.dp),
                    color = Accent,
                    strokeWidth = 2.dp
                )
                if (subscriptionState == SubscriptionState.Subscribing) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "One moment\u2026",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )
                }
            }
            is SubscriptionState.Error -> {
                Text(
                    text = subscriptionState.message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
                GoldButton(text = "Try again", onClick = onSubscribe)
            }
            else -> {
                GoldButton(
                    text = "Subscribe for nothing",
                    onClick = onSubscribe
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        if (subscriptionState !is SubscriptionState.Loading &&
            subscriptionState !is SubscriptionState.Subscribing
        ) {
            Text(
                text = "Already a supporter? Restore",
                style = MaterialTheme.typography.bodySmall,
                color = Accent.copy(alpha = 0.6f),
                modifier = Modifier
                    .clickable { onRestore() }
                    .padding(8.dp),
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "This app will never show you ads,\n" +
                    "never sell your data,\n" +
                    "and never pretend to do something it doesn\u2019t.\n" +
                    "Thank you.",
            style = MaterialTheme.typography.bodySmall,
            color = TextMuted.copy(alpha = 0.6f),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun PointRow(text: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = "\u2022",
            style = MaterialTheme.typography.bodyLarge,
            color = Accent.copy(alpha = 0.5f)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = TextSecondary
        )
    }
}
