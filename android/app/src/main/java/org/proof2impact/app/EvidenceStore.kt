package org.proof2impact.app

import android.content.Context
import java.io.BufferedInputStream
import java.io.ByteArrayInputStream
import java.io.File
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.security.MessageDigest
import java.time.Instant
import java.util.UUID

class EvidenceStore(private val context: Context) {
    private val root = File(context.filesDir, "evidence").apply { mkdirs() }

    fun persist(bytes: ByteArray, mimeType: String, source: String): EvidenceRecord =
        persist(ByteArrayInputStream(bytes), mimeType, source)

    fun persist(input: InputStream, mimeType: String, source: String): EvidenceRecord {
        require(mimeType.isNotBlank() && mimeType.length <= MAX_MIME_TYPE_LENGTH) { "Invalid MIME type" }
        require(source.isNotBlank() && source.length <= MAX_SOURCE_LENGTH) { "Invalid evidence source" }

        val id = UUID.randomUUID().toString()
        val target = File(root, id)
        val temporary = File(root, "$id.tmp")
        var size = 0L
        val digest = MessageDigest.getInstance("SHA-256")

        try {
            BufferedInputStream(input).use { stream ->
                temporary.outputStream().buffered().use { output ->
                    val buffer = ByteArray(BUFFER_SIZE)
                    while (true) {
                        val count = stream.read(buffer)
                        if (count < 0) break
                        size += count
                        require(size <= MAX_BYTES) { "Evidence exceeds pilot size limit" }
                        digest.update(buffer, 0, count)
                        output.write(buffer, 0, count)
                    }
                }
            }

            require(size > 0) { "Evidence is empty" }
            moveAtomically(temporary, target)
            return EvidenceRecord(
                id = id,
                fileName = target.name,
                mimeType = mimeType,
                source = source,
                sha256 = digest.digest().toHex(),
                capturedAt = Instant.now().toString(),
                sizeBytes = size,
            )
        } catch (error: Throwable) {
            temporary.delete()
            target.delete()
            throw error
        }
    }

    fun read(record: EvidenceRecord): ByteArray {
        require(record.fileName.matches(FILE_NAME_PATTERN)) { "Invalid evidence file name" }
        return File(root, record.fileName).readBytes()
    }

    private fun moveAtomically(source: File, target: File) {
        try {
            Files.move(
                source.toPath(),
                target.toPath(),
                StandardCopyOption.ATOMIC_MOVE,
                StandardCopyOption.REPLACE_EXISTING,
            )
        } catch (_: Exception) {
            check(source.renameTo(target)) { "Unable to finalize evidence file" }
        }
    }

    companion object {
        const val MAX_BYTES = 50L * 1024L * 1024L
        const val MAX_MIME_TYPE_LENGTH = 127
        const val MAX_SOURCE_LENGTH = 64
        private const val BUFFER_SIZE = 64 * 1024
        private val FILE_NAME_PATTERN = Regex("[0-9a-fA-F-]{36}")

        fun sha256(bytes: ByteArray): String =
            MessageDigest.getInstance("SHA-256").digest(bytes).toHex()
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
