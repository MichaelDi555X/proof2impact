package org.proof2impact.app

import android.content.Context
import java.io.File
import java.security.MessageDigest
import java.time.Instant
import java.util.UUID

class EvidenceStore(private val context: Context) {
    private val root = File(context.filesDir, "evidence").apply { mkdirs() }

    fun persist(bytes: ByteArray, mimeType: String, source: String): EvidenceRecord {
        require(bytes.isNotEmpty()) { "Evidence is empty" }
        require(bytes.size <= MAX_BYTES) { "Evidence exceeds pilot size limit" }
        val id = UUID.randomUUID().toString()
        val file = File(root, id)
        file.writeBytes(bytes)
        val hash = MessageDigest.getInstance("SHA-256").digest(bytes).toHex()
        return EvidenceRecord(id, file.name, mimeType, source, hash, Instant.now().toString(), bytes.size.toLong())
    }

    fun read(record: EvidenceRecord): ByteArray = File(root, record.fileName).readBytes()

    companion object {
        const val MAX_BYTES = 50L * 1024L * 1024L
    }
}

data class EvidenceRecord(
    val id: String,
    val fileName: String,
    val mimeType: String,
    val source: String,
    val sha256: String,
    val capturedAt: String,
    val sizeBytes: Long,
)

private fun ByteArray.toHex(): String = joinToString("") { "%02x".format(it) }
