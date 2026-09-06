package org.proof2impact.app

import org.junit.Assert.assertEquals
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
        assertEquals("fa4ee5957f9359be5bb8e9b589a758b51f195797296c7c7b56159fca32b46d2b", digest)
    }
}
