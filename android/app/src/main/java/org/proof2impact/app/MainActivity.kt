package org.proof2impact.app

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
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
import java.io.ByteArrayOutputStream

class MainActivity : ComponentActivity() {
    private val evidenceStore by lazy { EvidenceStore(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MaterialTheme { PilotApp() } }
    }

    @androidx.compose.runtime.Composable
    private fun PilotApp() {
        val records = remember { mutableStateListOf<EvidenceRecord>() }
        var status by remember { mutableStateOf("Local-first pilot. No evidence is uploaded automatically.") }

        val cameraPermission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            status = if (granted) "Camera permission granted. Tap Photo again to capture." else "Camera denied. You can continue without camera evidence."
        }
        val audioPermission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            status = if (granted) "Microphone permission granted. Tap Audio again to record." else "Microphone denied. You can continue without audio evidence."
        }
        val locationPermission = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
            status = if (result[Manifest.permission.ACCESS_FINE_LOCATION] == true || result[Manifest.permission.ACCESS_COARSE_LOCATION] == true) "Location permission granted for optional provenance." else "Location denied; GPS provenance will be omitted."
        }
        val photoCapture = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
            bitmap?.let {
                runCatching { evidenceStore.persist(it.toJpeg(), "image/jpeg", "camera") }
                    .onSuccess { records.add(it); status = "Photo stored locally and hashed: ${it.sha256.take(12)}…" }
                    .onFailure { status = "Photo could not be stored: ${it.message}" }
            }
        }
        val photoPicker = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            uri ?: return@rememberLauncherForActivityResult
            persistUri(uri.toString(), records, "photo-picker")
        }
        val audioCapture = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            result.data?.data?.let { persistUri(it.toString(), records, "audio-recorder") }
        }
        val videoCapture = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            result.data?.data?.let { persistUri(it.toString(), records, "video-recorder") }
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
                            Button(onClick = {
                                if (ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) photoCapture.launch(null)
                                else cameraPermission.launch(Manifest.permission.CAMERA)
                            }) { Text("Photo") }
                            OutlinedButton(onClick = { photoPicker.launch(ActivityResultContracts.PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo)) }) { Text("Library") }
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(onClick = {
                                if (ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) audioCapture.launch(Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION))
                                else audioPermission.launch(Manifest.permission.RECORD_AUDIO)
                            }) { Text("Audio") }
                            OutlinedButton(onClick = { videoCapture.launch(Intent(MediaStore.ACTION_VIDEO_CAPTURE)) }) { Text("Video") }
                        }
                        OutlinedButton(onClick = {
                            locationPermission.launch(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION))
                        }) { Text("Allow GPS provenance") }
                    }
                }
            }
            item {
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Verification", style = MaterialTheme.typography.titleLarge)
                        Text("Verification is human-reviewed; this pilot does not certify authenticity automatically.")
                        Button(onClick = { status = "Verification case prepared locally for future authenticated API submission." }) { Text("Prepare review") }
                        OutlinedButton(onClick = { authenticateForSensitiveAction { status = "Biometric authentication succeeded for this local action." } }) { Text("Test biometric gate") }
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
            items(records) { record -> Text("${record.source} • ${record.sizeBytes} bytes • SHA-256 ${record.sha256}") }
        }
    }

    private fun persistUri(uriString: String, records: MutableList<EvidenceRecord>, source: String) {
        val uri = android.net.Uri.parse(uriString)
        runCatching {
            val bytes = contentResolver.openInputStream(uri)?.use { it.readBytes() } ?: error("Unable to read selected media")
            evidenceStore.persist(bytes, contentResolver.getType(uri) ?: "application/octet-stream", source)
        }.onSuccess { records.add(it); statusToast("Evidence stored locally. SHA-256: ${it.sha256.take(12)}…") }
            .onFailure { statusToast("Evidence could not be stored: ${it.message}") }
    }

    private fun authenticateForSensitiveAction(onSuccess: () -> Unit) {
        val manager = BiometricManager.from(this)
        if (manager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG) != BiometricManager.BIOMETRIC_SUCCESS) {
            statusToast("Strong biometrics are not available on this device.")
            return
        }
        BiometricPrompt(this, object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) = onSuccess()
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) = statusToast("Biometric authentication cancelled or unavailable.")
        }).authenticate(
            BiometricPrompt.PromptInfo.Builder()
                .setTitle("Proof2Impact security check")
                .setSubtitle("Confirm your identity for this local sensitive action")
                .setNegativeButtonText("Cancel")
                .build(),
        )
    }

    private fun statusToast(message: String) = android.widget.Toast.makeText(this, message, android.widget.Toast.LENGTH_LONG).show()

    private fun enqueueSync() {
        val request = OneTimeWorkRequestBuilder<SyncWorker>()
            .setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
            .build()
        WorkManager.getInstance(this).enqueue(request)
    }
}

private fun Bitmap.toJpeg(): ByteArray = ByteArrayOutputStream().use { out ->
    compress(Bitmap.CompressFormat.JPEG, 90, out)
    out.toByteArray()
}
