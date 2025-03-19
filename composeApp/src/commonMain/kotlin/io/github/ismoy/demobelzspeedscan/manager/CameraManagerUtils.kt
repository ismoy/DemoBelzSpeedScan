package io.github.ismoy.demobelzspeedscan.manager

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import io.github.ismoy.belzspeedscan.CameraPreview
import io.github.ismoy.belzspeedscan.RequestCameraPermission
import io.github.ismoy.belzspeedscan.createBelSpeedScanCodeScanner
import io.github.ismoy.belzspeedscan.domain.CodeScanner

@Composable
fun CameraManagerUtils(
    context: Any?,
    onCodeScanned: (String) -> Unit
) {
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    var hasCameraPermission by remember { mutableStateOf(false) }
    val scanner: CodeScanner? by remember { mutableStateOf(null) }

    DisposableEffect(Unit) {
        onDispose {
            scanner?.stopScanning()
        }
    }

    RequestCameraPermission { granted ->
        hasCameraPermission = granted
    }

    Scaffold(
        content = { innerPadding ->
            if (hasCameraPermission) {
                Box(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ) {
                    Box {
                        var currentScanner by remember { mutableStateOf<CodeScanner?>(null) }
                        CameraPreview(
                            onPreviewViewReady = { preview ->
                                currentScanner = createBelSpeedScanCodeScanner(
                                    context = context,
                                    lifecycleOwner = lifecycleOwner,
                                    previewView = preview,
                                    playSound = true,
                                    resourceName = "beep",
                                    resourceExtension = "mp3",
                                    delayToNextScan = 3000,
                                    onCodeScanned = { scannedText ->
                                        onCodeScanned(scannedText)
                                    }
                                ).also {
                                    it.startScanning()
                                }
                            },
                            scanner = currentScanner,
                            modifier = Modifier.fillMaxHeight(1F),
                            tooFarText = "Acerca el código a la cámara\nDistancia demasiado lejana",
                            tooOptimalText = "¡Distancia perfecta!\nMantén el código dentro del marco",
                        )
                    }

                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .fillMaxHeight(1F)
                        .padding(innerPadding)
                        .background(Color.Black.copy(alpha = 0.8f))
                )
            }
        }
    )
}