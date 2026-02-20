package com.subs4what.app.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.subs4what.app.ui.theme.Accent
import com.subs4what.app.ui.theme.Outline
import com.subs4what.app.ui.theme.SurfaceCard
import com.subs4what.app.ui.theme.TextSecondary

@Composable
fun MemberBadge(
    memberNumber: Int,
    modifier: Modifier = Modifier
) {
    val formattedNumber = "#${memberNumber.toString().padStart(5, '0')}"

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = Outline,
                shape = RoundedCornerShape(20.dp)
            ),
        color = SurfaceCard,
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 28.dp, horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = "Supporter",
                style = MaterialTheme.typography.labelLarge,
                color = TextSecondary,
                letterSpacing = 2.sp
            )

            Text(
                text = formattedNumber,
                style = MaterialTheme.typography.displayLarge.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                ),
                color = Accent,
                textAlign = TextAlign.Center
            )
        }
    }
}
