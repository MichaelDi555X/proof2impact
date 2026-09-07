package org.proof2impact.app

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class SyncWorker(appContext: Context, params: WorkerParameters) : CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result {
        // Pilot-safe placeholder: never deletes local evidence and never claims upload success.
        // The production API client will replace this with authenticated, idempotent upload/finalize calls.
        return Result.success()
    }
}
