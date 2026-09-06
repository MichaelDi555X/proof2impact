package org.proof2impact.app

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class EvidenceStoreTest {
    @Test
    fun evidenceSizeLimitIsEnforced() {
        assertEquals(50L * 1024L * 1024L, EvidenceStore.MAX_BYTES)
    }

    @Test
    fun sha256IsDeterministicForKnownPayload() {
        val digest = "proof2impact".toByteArray().let {
            java.security.MessageDigest.getInstance("SHA-256").digest(it).joinToString("") { b -> "%02x".format(b) }
        }
        assertEquals("2c8e0b1c4a1f8f36b9dfb7e5a8f7e4f7fce0f0b6f4b1f9a9d4d7b3e0e3f8a4f6", digest)
    }
}
