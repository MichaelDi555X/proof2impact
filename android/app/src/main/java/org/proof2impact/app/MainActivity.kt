package org.proof2impact.app

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager

class MainActivity : ComponentActivity() {
    private val evidenceStore by lazy { EvidenceStore(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                PilotApp()
            }
        }
    }

    @androidx.compose.runtime.Composable
    private fun PilotApp() {
        val records = remember { mutableStateListOf<EvidenceRecord>() }
        var status by remember { mutableStateOf("Local-first pilot. No evidence is uploaded automatically.") }

        val cameraPermission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) capturePhoto(records) else status = "Camera denied. You can continue without camera evidence."
        }
        val audioPermission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) captureAudio(records) else status = "Microphone denied. You can continue without audio evidence."
        }
        val videoLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) status = "Video captured. Production upload/finalization is not yet enabled."
        }
        val photoPicker = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            uri ?: return@rememberLauncherForActivityResult
            val bytes = contentResolver.openInputStream(uri)?.use { it.readBytes() } ?: return@rememberLauncherForActivityResult
            runCatching { evidenceStore.persist(bytes, contentResolver.getType(uri) ?: "image/*", "photo-picker") }
                .onSuccess { records.add(it); status = "Photo stored locally and hashed: ${it.sha256.take(12)}…" }
                .onFailure { status = "Photo could not be stored: ${it.message}" }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                Text("Proof2Impact Pilot", style = MaterialTheme.typography.headlineMedium)
                Text("Evidence → verification → impact", style = MaterialTheme.typography.bodyMedium)
                Text(status, modifier = Modifier.padding(top = 8.dp))
            }
            item {
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Mission", style = MaterialTheme.typography.titleLarge)
                        Text("Pilot workspace for mission, milestone, evidence and verification preparation.")
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(onClick = { status = "Mission draft created locally. Server persistence is pending pilot backend." }) { Text("Create mission") }
                            OutlinedButton(onClick = { status = "Milestone draft created locally. Server persistence is pending pilot backend." }) { Text("Add milestone") }
                        }
                    }
                }
            }
            item {
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Evidence capture", style = MaterialTheme.typography.titleLarge)
                        Text("Permissions are requested only when the corresponding capture action is used.")
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(onClick = { requestCamera(cameraPermission) }) { Text("Photo") }
                            OutlinedButton(onClick = { photoPicker.launch(ActivityResultContracts.PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo)) }) { Text("Library") }
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(onClick = { requestAudio(audioPermission) }) { Text("Audio") }
                            OutlinedButton(onClick = { videoLauncher.launch(Intent(MediaStore.ACTION_VIDEO_CAPTURE)) }) { Text("Video") }
                        }
                        OutlinedButton(onClick = { status = locationStatus() }) { Text("Check GPS permission") }
                    }
                }
            }
            item {
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Verification", style = MaterialTheme.typography.titleLarge)
                        Text("Verification is human-reviewed; this pilot does not certify authenticity automatically.")
                        Button(onClick = { status = "Verification case queued locally for future authenticated API submission." }) { Text("Prepare review") }
                    }
                }
            }
            item {
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Offline queue", style = MaterialTheme.typography.titleLarge)
                        Text("${records.size} local evidence item(s). Local files are retained until an authenticated sync implementation is enabled.")
                        Button(onClick = { enqueueSync(); status = "Sync worker scheduled for network availability." }) { Text("Schedule sync") }
                    }
                }
            }
            items(records) { record ->
                Text("${record.source} • ${record.sizeBytes} bytes • SHA-256 ${record.sha256}")
            }
        }
    }

    private fun requestCamera(launcher: androidx.activity.result.ActivityResultLauncher<String>) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            // A real camera URI flow should use a FileProvider before pilot distribution.
            capturePhoto(mutableStateListOf())
        } else launcher.launch(Manifest.permission.CAMERA)
    }

    private fun requestAudio(launcher: androidx.activity.result.ActivityResultLauncher<String>) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) captureAudio(mutableStateListOf())
        else launcher.launch(Manifest.permission.RECORD_AUDIO)
    }

    private fun capturePhoto(records: MutableList<EvidenceRecord>) {
        statusToast("Camera permission granted. Camera capture URI hardening is required before pilot release.")
    }

    private fun captureAudio(records: MutableList<EvidenceRecord>) {
        statusToast("Microphone permission granted. Audio recorder integration is required before pilot release.")
    }

    private fun statusToast(message: String) = android.widget.Toast.makeText(this, message, android.widget.Toast.LENGTH_LONG).show()

    private fun locationStatus(): String = when {
        ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED -> "Precise location permission is granted. Capture only with explicit user consent."
        ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED -> "Approximate location permission is granted."
        else -> "Location permission is not granted; GPS provenance will be omitted."
    }

    private fun enqueueSync() {
        val request = OneTimeWorkRequestBuilder<SyncWorker>()
            .setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
            .build()
        WorkManager.getInstance(this).enqueue(request)
    }
}
