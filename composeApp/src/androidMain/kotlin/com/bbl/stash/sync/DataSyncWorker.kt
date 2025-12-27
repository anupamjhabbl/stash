package com.bbl.stash.sync

import android.content.Context
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.bbl.stash.common.Constants
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.concurrent.TimeUnit

class DataSyncWorker(
    context: Context,
    workerParameters: WorkerParameters
) : CoroutineWorker(context, workerParameters), KoinComponent {
    private val stashSyncManager: StashSyncManager by inject()

    override suspend fun doWork(): Result {
        try {
            stashSyncManager.syncData()
            return Result.success()
        } catch (_: Exception) {
            return Result.failure()
        }
    }

    companion object {
        fun initWorker(context: Context) {
            val workManagerBuilder = PeriodicWorkRequestBuilder<DataSyncWorker>(
                repeatInterval = Constants.TASK_TIMER_MINUTES,
                repeatIntervalTimeUnit = TimeUnit.MINUTES
            ).setConstraints(
                Constraints.Builder()
                    .setRequiresBatteryNotLow(true)
                    .build()
            )

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                uniqueWorkName = Constants.TASK_IDENTIFIER,
                existingPeriodicWorkPolicy = ExistingPeriodicWorkPolicy.KEEP,
                request = workManagerBuilder.build()
            )
        }

        fun stopWorker(context: Context) {
            WorkManager.getInstance(context)
                .cancelUniqueWork(Constants.TASK_IDENTIFIER)
        }
    }
}