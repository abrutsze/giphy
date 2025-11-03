package com.android.ui.components.buttons

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.android.ui.theme.AppColors
import com.android.ui.theme.AppTypography
import com.android.ui.theme.emphasized_W600


/**
 * A customizable primary button composable.
 *
 * This button provides:
 * - A rounded rectangular shape.
 * - Dynamic colors that adjust based on the `isEnabled` state.
 * - Text styled with emphasis, centered vertically with padding.
 * - A full-width layout by default.
 *
 * @param modifier The modifier to be applied to the button.
 * @param text The text displayed on the button.
 * @param onClick The action triggered when the button is clicked.
 * @param isEnabled Determines whether the button is enabled or disabled (default is true).
 */

@Composable
fun PrimaryButton(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit,
    isEnabled: Boolean = true
) {
    val containerColor =
        if (isEnabled) AppColors.primaryBlue100 else AppColors.grey300
    val contentColor = if (isEnabled) Color.White else AppColors.grey800

    Button (
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        enabled = isEnabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        contentPadding = PaddingValues(0.dp),
        modifier = modifier
            .fillMaxWidth()
    ) {
        Text(
            modifier = Modifier.padding(vertical = 13.dp),
            text = text,
            style = AppTypography.bodyMedium.emphasized_W600
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PrimaryButtonPreview() {
    PrimaryButton(text = "confirm", onClick = {}, isEnabled = true)
}

@Preview(showBackground = true)
@Composable
fun PrimaryButtonDisabledPreview() {
    PrimaryButton(text = "confirm", onClick = {}, isEnabled = false)
}
