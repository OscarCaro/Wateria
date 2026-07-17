package com.wateria.revamp.design

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wateria.R

@Composable
fun WateriaBackButton(onClick: () -> Unit) {
    val description = stringResource(R.string.revamp_navigate_back)
    Box(
        contentAlignment = Alignment.Center,
        modifier =
            Modifier
                .size(48.dp)
                .semantics { contentDescription = description }
                .clickable(role = Role.Button, onClick = onClick)
    ) {
        Text(
            text = "‹",
            modifier = Modifier.clearAndSetSemantics { },
            style = MaterialTheme.typography.headlineLarge.copy(fontSize = 38.sp),
            color = MaterialTheme.colorScheme.onPrimary
        )
    }
}
