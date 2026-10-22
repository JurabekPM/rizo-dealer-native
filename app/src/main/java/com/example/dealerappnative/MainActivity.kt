package com.example.dealerappnative

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.dealerappnative.navigation.DealerDestination

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Device checks can choose the initial administrator tab after role selection.
        val initialTab = intent.getIntExtra("previewTab", 0)
            .coerceIn(0, DealerDestination.entries.lastIndex)
        setContent { DealerEntry(initialTab) }
    }
}
