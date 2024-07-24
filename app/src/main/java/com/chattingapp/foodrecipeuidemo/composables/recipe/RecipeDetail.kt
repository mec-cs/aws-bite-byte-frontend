package com.chattingapp.foodrecipeuidemo.composables.recipe

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun RecipeDetail(fieldName: String, detail: String?) {
    // Prepare formatted detail with bullet points
    val formattedDetail = detail?.let {
        if (fieldName == "Ingredients" || fieldName == "Instructions") {
            it.split(".").joinToString(".\n") { line ->
                if (line.isNotBlank()) {
                    "• ${line.trim()}"
                } else {
                    "" // Avoid adding bullets for empty lines
                }
            }
        } else {
            it
        }
    } ?: "No $fieldName available"

    Text(
        text = buildAnnotatedString {
            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                append("$fieldName:\n")
            }
            // Append the formatted detail with bold bullets
            AppendBoldBullets(formattedDetail)
        },
        modifier = Modifier.padding(vertical = 12.dp),
        lineHeight = 20.sp // Adjust line height to avoid overlap or excessive spacing
    )
}

@Composable
private fun AnnotatedString.Builder.AppendBoldBullets(text: String) {
    text.split("•").forEach { line ->
        if (line.isNotBlank()) {
            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                append("• ")
            }
            append(line.trim())
            append("\n")
        }
    }
}
