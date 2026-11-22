package com.example.testpilot

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import androidx.compose.ui.graphics.Color
import com.example.testpilot.ui.navigation.TestPilotApp
import com.example.testpilot.ui.theme.TestPilotTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            TestPilotTheme {
                Surface(
                    color = Color.Transparent
                ) {
                    TestPilotApp()
                }
            }
        }
    }
}