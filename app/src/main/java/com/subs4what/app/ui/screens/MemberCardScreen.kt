package com.subs4what.app.ui.screens

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.subs4what.app.ui.components.GoldButton
import com.subs4what.app.ui.theme.Accent
import com.subs4what.app.ui.theme.Outline
import com.subs4what.app.ui.theme.SurfaceCard
import com.subs4what.app.ui.theme.TextMuted
import com.subs4what.app.ui.theme.TextSecondary

@Composable
fun MemberCardScreen(
    memberNumber: Int,
    memberSince: String,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val formattedNumber = "#${memberNumber.toString().padStart(5, '0')}"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(36.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = Outline,
                    shape = RoundedCornerShape(24.dp)
                ),
            color = SurfaceCard,
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "subs4what",
                    style = MaterialTheme.typography.labelLarge.copy(
                        letterSpacing = 3.sp
                    ),
                    color = TextMuted
                )

                HorizontalDivider(
                    color = Outline,
                    thickness = 0.5.dp
                )

                Text(
                    text = "Supporter",
                    style = MaterialTheme.typography.titleSmall,
                    color = TextSecondary
                )

                Text(
                    text = formattedNumber,
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp,
                        fontSize = 56.sp
                    ),
                    color = Accent
                )

                HorizontalDivider(
                    color = Outline,
                    thickness = 0.5.dp
                )

                Text(
                    text = "Since $memberSince",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted
                )

                Text(
                    text = "This person pays for nothing\nso the internet can stay free.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        GoldButton(
            text = "Share",
            onClick = { shareCard(context, memberNumber) }
        )

        Spacer(modifier = Modifier.height(12.dp))

        TextButton(onClick = onBack) {
            Text(
                text = "Back",
                style = MaterialTheme.typography.labelLarge,
                color = TextMuted
            )
        }
    }
}

private fun shareCard(context: Context, memberNumber: Int) {
    val text = buildString {
        appendLine("I\u2019m supporter #${memberNumber.toString().padStart(5, '0')} on Subs4What.")
        appendLine()
        appendLine("I pay \$1/month for nothing \u2014 so the internet stays free.")
        appendLine()
        appendLine("#subs4what")
    }

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, text)
    }
    context.startActivity(Intent.createChooser(intent, "Share"))
}
