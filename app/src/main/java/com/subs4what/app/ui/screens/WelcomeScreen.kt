package com.subs4what.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.subs4what.app.ui.components.GoldButton
import com.subs4what.app.ui.components.NothingAnimation
import com.subs4what.app.ui.theme.Accent
import com.subs4what.app.ui.theme.TextMuted
import com.subs4what.app.ui.theme.TextSecondary
import kotlinx.coroutines.delay

@Composable
fun WelcomeScreen(onBeginJourney: () -> Unit) {
    var showTitle by remember { mutableStateOf(false) }
    var showBody by remember { mutableStateOf(false) }
    var showNote by remember { mutableStateOf(false) }
    var showButton by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(400)
        showTitle = true
        delay(700)
        showBody = true
        delay(700)
        showNote = true
        delay(700)
        showButton = true
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 36.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        NothingAnimation()

        Spacer(modifier = Modifier.height(48.dp))

        AnimatedVisibility(
            visible = showTitle,
            enter = fadeIn() + slideInVertically { it / 3 }
        ) {
            Text(
                text = "This app does nothing.",
                style = MaterialTheme.typography.headlineMedium,
                color = Accent,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        AnimatedVisibility(
            visible = showBody,
            enter = fadeIn() + slideInVertically { it / 3 }
        ) {
            Text(
                text = "No features. No content. No notifications.\n" +
                        "Just a quiet \$1 a month that goes toward\n" +
                        "keeping the internet open and free.",
                style = MaterialTheme.typography.bodyLarge,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        AnimatedVisibility(
            visible = showNote,
            enter = fadeIn() + slideInVertically { it / 3 }
        ) {
            Text(
                text = "Nothing will happen. That\u2019s the point.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextMuted,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(48.dp))

        AnimatedVisibility(
            visible = showButton,
            enter = fadeIn() + slideInVertically { it / 3 }
        ) {
            GoldButton(
                text = "Support the free internet",
                onClick = onBeginJourney,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}
