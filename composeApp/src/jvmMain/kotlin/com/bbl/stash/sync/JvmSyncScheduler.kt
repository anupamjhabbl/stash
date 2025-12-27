package com.bbl.stash.sync

import com.bbl.stash.domain.repository.StashRemoteRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.minutes

class JvmSyncScheduler(
    repository: StashRemoteRepository,
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
) {
    val stashDataSync = StashDataSync(repository)

    fun start() {
        scope.launch {
            while (isActive) {
                try {
                    stashDataSync.syncData()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                delay(15.minutes)
            }
        }
    }

    fun stop() {
        scope.cancel()
    }
}
