package org.proof2impact.app

import org.junit.Assert.assertEquals
import org.junit.Test

class EvidenceStoreTest {
    @Test
    fun evidenceSizeLimitIsEnforced() {
        assertEquals(50L * 1024L * 1024L, EvidenceStore.MAX_BYTES)
    }

    @Test
    fun sha256UsesProductionImplementation() {
        val payload = "proof2impact".toByteArray()
        assertEquals(
            "fa4ee5957f9359be5bb8e9b589a758b51f195797296c7c7b56159fca32b46d2b",
            EvidenceStore.sha256(payload),
        )
    }

    @Test
    fun evidenceMetadataLimitsAreStable() {
        assertEquals(127, EvidenceStore.MAX_MIME_TYPE_LENGTH)
        assertEquals(64, EvidenceStore.MAX_SOURCE_LENGTH)
    }
}
