package com.david.collegeevents.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

fun generateColorFromName(name: String): Color {
    val colors = listOf(
        Color(0xFFEF5350),
        Color(0xFFAB47BC),
        Color(0xFF5C6BC0),
        Color(0xFF29B6F6),
        Color(0xFF66BB6A),
        Color(0xFFFFCA28),
        Color(0xFFFF7043)
    )

    val hash = name.hashCode()
    return colors[kotlin.math.abs(hash) % colors.size]
}

@Composable
fun NameAvatar(
    name: String,
    modifier: Modifier = Modifier,
    size: Dp = 40.dp
) {
    val initials = remember(name) {
        val words = name
            .trim()
            .split("\\s+".toRegex())
            .filter { it.isNotBlank() }

        when {
            words.isEmpty() -> ""
            words.size == 1 -> {
                words[0].take(2).uppercase()
            }

            else -> {
                "${words.first().first()}${words.last().first()}".uppercase()
            }
        }
    }

    val backgroundColor = remember(name) {
        generateColorFromName(name)
    }

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initials,
            color = Color.White,
            fontSize = (size.value / 2.3).sp,
            fontWeight = FontWeight.Bold
        )
    }
}

