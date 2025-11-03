package com.android.ui.components.toolbar
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.android.resources.R
import com.android.ui.preview.ProjectPreviewComponentTheme
import com.android.ui.theme.AppColors
import com.android.ui.theme.AppTypography
import com.android.ui.theme.emphasized_W600
import com.android.ui.theme.regular

@Composable
fun Toolbar(
    title: String,
    modifier: Modifier = Modifier,
    onBackClick: (() -> Unit)? = null,
    backBtnModifier: Modifier = Modifier,
    icon: Int = R.drawable.ic_arrow_back_top_bar,
    backgroundColor: Color = AppColors.white,
    titleColor: Color = AppColors.grey900Text
) {

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = backgroundColor
            )
            .padding(vertical = 12.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = if (onBackClick != null) Arrangement.Start else Arrangement.Center
    ) {
        if (onBackClick != null) {
            IconButton(
                onClick = onBackClick,
                modifier = backBtnModifier
            ) {
                Icon(
                    painter = painterResource(id = icon),
                    contentDescription = "Back",
                    tint = AppColors.grey800
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(
            text = title,
            style = AppTypography.titleLarge.regular,
            color = titleColor
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ToolbarPreview() {
    ProjectPreviewComponentTheme {
        Toolbar(title = "Profile")
    }
}