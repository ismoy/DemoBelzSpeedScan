package io.github.ismoy.demobelzspeedscan.ui

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@Composable
actual fun getPlatformAlertManager(): AlertManager = object : AlertManager {
    val context = LocalContext.current
    override fun showAlert(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}