package com.slvpro

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocumentScannerScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var showDocumentScanner by remember { mutableStateOf(false) }
    var scannedUri by remember { mutableStateOf<Uri?>(null) }
    var cameraError by remember { mutableStateOf(false) }

    val cameraLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.TakePicturePreview()
        ) { bitmap ->

            if (bitmap != null) {
                val path = MediaStore.Images.Media.insertImage(
                    context.contentResolver,
                    bitmap,
                    "SLV-PRO Document",
                    "Scanned vehicle document"
                )

                if (path != null) {
                    scannedUri = Uri.parse(path)
                }
            } else {
                cameraError = true
            }
        }

    val permissionLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { granted ->

            if (granted) {
                cameraLauncher.launch(null)
            } else {
                cameraError = true
            }
        }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("📄 Document Scanner")
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(30.dp))

            Icon(
                imageVector = Icons.Default.CameraAlt,
                contentDescription = null,
                modifier = Modifier.size(80.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Scan Vehicle Document",
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Use the camera to scan PUC, Insurance, Fitness, Permit or other vehicle documents.",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(30.dp))

            Button(
                onClick = {
                    cameraError = false

                    if (
                        ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.CAMERA
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        cameraLauncher.launch(null)
                    } else {
                        permissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = null
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text("Open Camera & Scan")
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (scannedUri != null) {
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "✅ Document Scanned",
                            style = MaterialTheme.typography.titleMedium
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "The scanned document image has been captured."
                        )
                    }
                }
            }

            if (cameraError) {
                Spacer(modifier = Modifier.height(15.dp))

                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "⚠️ Camera permission is required to scan documents.",
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}
