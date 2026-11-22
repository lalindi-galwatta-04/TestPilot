package com.example.testpilot.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.testpilot.ui.theme.PurpleSecondary
import com.example.testpilot.ui.theme.TextSecondary

@Composable
fun PlaceholderScreen(
    title: String
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = title,
            color = PurpleSecondary,
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "This module will be implemented in an upcoming milestone.",
            color = TextSecondary,
            fontSize = 14.sp
        )
    }
}