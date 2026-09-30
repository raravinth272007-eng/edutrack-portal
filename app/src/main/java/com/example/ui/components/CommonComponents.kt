package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.BloodGroup
import com.example.model.UrgencyLevel
import com.example.ui.theme.AlertOrange
import com.example.ui.theme.AlertOrangeContainer
import com.example.ui.theme.CrimsonDark
import com.example.ui.theme.CrimsonRed
import com.example.ui.theme.RoseContainer
import com.example.ui.theme.SafeGreen
import com.example.ui.theme.SafeGreenContainer
import com.example.ui.theme.UrgentCriticalRed
import com.example.ui.theme.UrgentPlannedBlue
import com.example.ui.theme.UrgentWarningAmber

object IntentHelper {
    fun dialPhone(context: Context, phoneNumber: String) {
        try {
            val cleanPhone = phoneNumber.replace(" ", "").replace("-", "")
            val intent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:$cleanPhone")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Could not open dialer for $phoneNumber", Toast.LENGTH_SHORT).show()
        }
    }

    fun openDirections(context: Context, latitude: Double, longitude: Double, label: String) {
        try {
            val uri = Uri.parse("geo:$latitude,$longitude?q=$latitude,$longitude($label)")
            val mapIntent = Intent(Intent.ACTION_VIEW, uri).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(mapIntent)
        } catch (e: Exception) {
            // Web fallback
            try {
                val webUri = Uri.parse("https://www.google.com/maps/search/?api=1&query=$latitude,$longitude")
                val webIntent = Intent(Intent.ACTION_VIEW, webUri).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(webIntent)
            } catch (err: Exception) {
                Toast.makeText(context, "Location: $latitude, $longitude ($label)", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun openWhatsApp(context: Context, phoneNumber: String, message: String) {
        try {
            val cleanPhone = phoneNumber.replace("+", "").replace(" ", "").replace("-", "")
            val url = "https://api.whatsapp.com/send?phone=$cleanPhone&text=${Uri.encode(message)}"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "WhatsApp message to $phoneNumber: $message", Toast.LENGTH_SHORT).show()
        }
    }
}

@Composable
fun BloodGroupBadge(
    bloodGroup: String,
    modifier: Modifier = Modifier,
    size: Dp = 44.dp,
    isUniversal: Boolean = bloodGroup.contains("O−") || bloodGroup.contains("O-")
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(if (isUniversal) CrimsonDark else CrimsonRed)
            .border(2.dp, if (isUniversal) Color(0xFFFFD700) else Color.White, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = bloodGroup,
            color = Color.White,
            fontWeight = FontWeight.Black,
            fontSize = if (size > 40.dp) 15.sp else 12.sp
        )
    }
}

@Composable
fun UrgencyBadge(
    urgency: UrgencyLevel,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor, label) = when (urgency) {
        UrgencyLevel.CRITICAL -> Triple(RoseContainer, UrgentCriticalRed, "CRITICAL")
        UrgencyLevel.WITHIN_24H -> Triple(AlertOrangeContainer, AlertOrange, "WITHIN 24H")
        UrgencyLevel.PLANNED -> Triple(Color(0xFFE0F2FE), UrgentPlannedBlue, "PLANNED")
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = bgColor
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(textColor)
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = label,
                color = textColor,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    count: Int? = null,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )
        if (count != null) {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.padding(horizontal = 6.dp)
            ) {
                Text(
                    text = count.toString(),
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                )
            }
        }
        if (actionText != null && onActionClick != null) {
            OutlinedButton(
                onClick = onActionClick,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.height(34.dp)
            ) {
                Text(text = actionText, fontSize = 12.sp)
            }
        }
    }
}
