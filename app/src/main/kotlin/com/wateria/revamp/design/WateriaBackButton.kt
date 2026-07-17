package com.wateria.revamp.design

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.wateria.R

@Composable
fun WateriaBackButton(onClick: () -> Unit) {
    val description = stringResource(R.string.revamp_navigate_back)
    TextButton(
        onClick = onClick,
        modifier = Modifier.semantics { contentDescription = description }
    ) {
        Text(
            text = "‹",
            modifier = Modifier.clearAndSetSemantics { },
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onPrimary
        )
    }
}
