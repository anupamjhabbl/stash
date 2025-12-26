package com.example.stash.sync

import android.content.Context
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.stash.common.Constants
import com.example.stash.domain.repository.StashRemoteRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.concurrent.TimeUnit

class DataSyncWorker(
    context: Context,
    workerParameters: WorkerParameters
) : CoroutineWorker(context, workerParameters), KoinComponent {
    private val repository: StashRemoteRepository by inject()

    override suspend fun doWork(): Result {
        StashDataSync(repository).syncData()
        return Result.success()
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