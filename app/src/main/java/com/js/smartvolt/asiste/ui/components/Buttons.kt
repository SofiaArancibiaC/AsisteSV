package com.js.smartvolt.asiste.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun GradientButton(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    val gradiente = Brush.horizontalGradient(listOf(Color(0xFFE6FF0A), Color(0xFFFF6A2B)))
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(16.dp),
        contentPadding = PaddingValues(0.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        modifier = modifier.fillMaxWidth().height(54.dp).background(gradiente, RoundedCornerShape(16.dp))
    ) {
        Text(text, color = Color(0xFF111111), fontWeight = androidx.compose.ui.text.font.FontWeight.ExtraBold)
    }
}