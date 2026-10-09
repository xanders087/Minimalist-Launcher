package com.example.ui.theme

import androidx.compose.ui.graphics.Color

fun getCategoryColor(category: String): Color {
    return when (category.lowercase()) {
        "communication" -> Color(0xFF9CD67D)
        "productivity" -> Color(0xFFFFD54F)
        "media" -> Color(0xFFA1A1AA)
        "games" -> Color(0xFFF4F4F5)
        else -> Color(0xFF71717A)
    }
}
