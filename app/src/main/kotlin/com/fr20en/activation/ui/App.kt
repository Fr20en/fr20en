package com.fr20en.activation.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import com.fr20en.activation.model.AppState
import com.fr20en.activation.ui.theme.AppTheme

val LocalAppState = staticCompositionLocalOf { AppState() }

@Composable
fun App() {
    var appState by remember { mutableStateOf(AppState()) }

    AppTheme {
        CompositionLocalProvider(
            LocalAppState provides appState
        ) {
            MainScreen(
                appState = appState,
                onAppStateChange = { appState = it },
            )
        }
    }
}