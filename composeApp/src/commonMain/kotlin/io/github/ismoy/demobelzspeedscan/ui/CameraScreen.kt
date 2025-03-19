package io.github.ismoy.demobelzspeedscan.ui


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.BottomSheetValue
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.material.rememberBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.ismoy.demobelzspeedscan.manager.CameraManagerUtils
import kotlinx.coroutines.launch

interface AlertManager {
    fun showAlert(message: String)
}

@Composable
expect fun getPlatformAlertManager(): AlertManager

@Composable
fun CameraScreen(context: Any?) {
    val scannedCodes = remember { mutableStateListOf<String>() }
    val alertManager = getPlatformAlertManager()
    val coroutineScope = rememberCoroutineScope()

    val bottomSheetState = rememberBottomSheetState(
        initialValue = BottomSheetValue.Collapsed
    )
    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = bottomSheetState
    )

    LaunchedEffect(scannedCodes.size) {
        if (scannedCodes.isNotEmpty() && bottomSheetState.isCollapsed) {
            coroutineScope.launch {
                bottomSheetState.expand()
            }
        }
    }

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                title = { Text("BelzSpeedScan") }
            )
        },
        sheetContent = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(
                        onClick = {
                            coroutineScope.launch {
                                if (bottomSheetState.isCollapsed) bottomSheetState.expand()
                                else bottomSheetState.collapse()
                            }
                        }
                    ) {
                        Icon(
                            Icons.Default.KeyboardArrowUp,
                            contentDescription = "Expandir/Colapsar lista"
                        )
                    }
                }

                Text(
                    text = "Códigos escaneados (${scannedCodes.size})",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    textAlign = TextAlign.Center
                )

                Divider()

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                ) {
                    items(scannedCodes) { code ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                        ) {
                            Text(
                                text = code,
                                modifier = Modifier.align(Alignment.CenterStart)
                            )
                            IconButton(
                                onClick = {
                                    scannedCodes.remove(code)
                                    if (scannedCodes.isEmpty()) {
                                        coroutineScope.launch {
                                            bottomSheetState.collapse()
                                        }
                                    }
                                },
                                modifier = Modifier.align(Alignment.CenterEnd)
                            ) {
                                Icon(Icons.Default.Clear, contentDescription = "Eliminar código")
                            }
                        }
                        Divider()
                    }
                }
            }
        },
        sheetPeekHeight = if (scannedCodes.isEmpty()) 0.dp else 60.dp
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            CameraManagerUtils(context) { codeScanned ->
                if (codeScanned.isNotEmpty()) {
                    if (scannedCodes.contains(codeScanned)) {
                        alertManager.showAlert("Código duplicado: $codeScanned")
                    } else {
                        scannedCodes.add(codeScanned)
                        alertManager.showAlert("Código escaneado: $codeScanned")
                        coroutineScope.launch {
                            bottomSheetState.expand()
                        }
                    }
                }
            }
        }
    }
}