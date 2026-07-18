@file:Suppress("MagicNumber", "LongParameterList")

package com.wateria.revamp.design

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

val WateriaPanelShape = RoundedCornerShape(32.dp)
val WateriaPillShape = RoundedCornerShape(percent = 50)
val WateriaDialogButtonHeight = 45.dp

@Composable
fun WateriaScreenHeader(
    title: String,
    modifier: Modifier = Modifier,
    onBack: (() -> Unit)? = null,
    action: (@Composable RowScope.() -> Unit)? = null
) {
    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .height(53.dp)
                .background(MaterialTheme.colorScheme.primary)
                .padding(horizontal = 4.dp)
    ) {
        if (onBack != null) {
            Box(modifier = Modifier.align(Alignment.CenterStart)) {
                WateriaBackButton(onClick = onBack)
            }
        }

        Text(
            text = title.uppercase(),
            style =
                MaterialTheme.typography.headlineLarge.copy(
                    fontSize = 24.sp,
                    lineHeight = 33.sp
                ),
            color = MaterialTheme.colorScheme.onPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            modifier =
                Modifier
                    .align(Alignment.Center)
                    .padding(horizontal = 56.dp)
        )

        if (action != null) {
            Row(
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
                content = action,
                modifier = Modifier.align(Alignment.CenterEnd)
            )
        }
    }
}

@Composable
fun WateriaPillButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
    enabled: Boolean = true,
    filled: Boolean = true,
    height: Dp = WateriaDialogButtonHeight,
    containerColor: Color = if (filled) color else Color.Transparent,
    border: BorderStroke? = null,
    textStyle: TextStyle =
        MaterialTheme.typography.titleLarge.copy(
            fontSize = 22.sp,
            lineHeight = 28.sp
        )
) {
    if (filled) {
        Button(
            onClick = onClick,
            enabled = enabled,
            shape = WateriaPillShape,
            colors =
                ButtonDefaults.buttonColors(
                    containerColor = containerColor,
                    contentColor = Color.White,
                    disabledContainerColor = containerColor.copy(alpha = 0.35f)
                ),
            border = border,
            modifier = modifier.height(height)
        ) {
            Text(text = text, style = textStyle)
        }
    } else {
        OutlinedButton(
            onClick = onClick,
            enabled = enabled,
            shape = WateriaPillShape,
            border = border ?: BorderStroke(2.dp, color),
            colors =
                ButtonDefaults.outlinedButtonColors(
                    containerColor = containerColor,
                    contentColor = color
                ),
            modifier = modifier.height(height)
        ) {
            Text(text = text, style = textStyle)
        }
    }
}

@Composable
fun WateriaDialog(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    dismissOnBackPress: Boolean = true,
    dismissOnClickOutside: Boolean = true,
    contentPadding: PaddingValues = PaddingValues(horizontal = 8.dp, vertical = 24.dp),
    content: @Composable ColumnScope.() -> Unit
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties =
            DialogProperties(
                dismissOnBackPress = dismissOnBackPress,
                dismissOnClickOutside = dismissOnClickOutside,
                usePlatformDefaultWidth = false
            )
    ) {
        Surface(
            shape = WateriaPanelShape,
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 12.dp,
            modifier =
                modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
        ) {
            androidx.compose.foundation.layout.Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                content = content,
                modifier = Modifier.padding(contentPadding)
            )
        }
    }
}

@Composable
fun WateriaLegacyAlertDialog(
    title: String,
    body: String,
    confirmLabel: String,
    dismissLabel: String,
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit,
    confirmColor: Color = MaterialTheme.colorScheme.primary
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            color = Color.White,
            shape = RoundedCornerShape(2.dp),
            shadowElevation = 10.dp,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 27.dp)
        ) {
            androidx.compose.foundation.layout.Column(
                modifier = Modifier.fillMaxWidth().padding(top = 18.dp, bottom = 4.dp)
            ) {
                Text(
                    text = title,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp)
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    text = body,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodyLarge.copy(fontSize = 15.sp),
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp)
                )
                Spacer(Modifier.height(32.dp))
                Row(
                    modifier = Modifier.fillMaxWidth().height(54.dp).padding(end = 8.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    androidx.compose.material3.TextButton(onClick = onDismissRequest) {
                        Text(
                            text = dismissLabel.uppercase(),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    androidx.compose.material3.TextButton(onClick = onConfirm) {
                        Text(text = confirmLabel.uppercase(), color = confirmColor)
                    }
                }
            }
        }
    }
}

@Composable
fun WateriaGreenDivider(modifier: Modifier = Modifier) {
    Spacer(
        modifier =
            modifier
                .fillMaxWidth()
                .height(2.dp)
                .background(MaterialTheme.colorScheme.primary)
    )
}
