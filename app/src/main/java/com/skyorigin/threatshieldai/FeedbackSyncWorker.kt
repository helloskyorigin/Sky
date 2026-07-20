package com.skyorigin.threatshieldai

import android.content.Context
import android.util.Log
import androidx.work.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldValue
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import com.google.android.gms.tasks.Task

suspend fun <T> Task<T>.await(): T = suspendCancellableCoroutine { cont ->
    addOnCompleteListener { task ->
        if (task.isSuccessful) {
cont.resume(task.result)
        } else {
cont.resumeWithException(task.exception ?: RuntimeException("Unknown task error"))
        }
    }
}

class FeedbackSyncWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val database = AppDatabase.getDatabase(context)
        val feedbackDao = database.feedbackDao()
        val pendingList = feedbackDao.getAllPendingFeedback()
        
        if (pendingList.isEmpty()) {
            return Result.success()
        }
        
        val firestore = try {
            FirebaseFirestore.getInstance()
        } catch (e: Exception) {
            Log.e("FeedbackSyncWorker", "Firebase not initialized: ", e)
            return Result.retry()
        }

        var allSuccessful = true

        for (feedback in pendingList) {
            try {
                val uid = ""
                val categoryLower = (feedback.category ?: "").lowercase()
                val collectionName = when {
                    categoryLower.contains("bug") || categoryLower.contains("बग") -> "bugReports"
                    categoryLower.contains("feature") || categoryLower.contains("request") || categoryLower.contains("सुझाव") -> "featureRequests"
                    else -> "feedback"
                }

                val data = hashMapOf(
                    "uid" to uid,
                    "feedbackId" to feedback.feedbackId,
                    "rating" to feedback.rating,
                    "category" to (feedback.category ?: ""),
                    "message" to (feedback.message ?: ""),
                    "appVersion" to feedback.appVersion,
                    "androidVersion" to feedback.androidVersion,
                    "deviceModel" to feedback.deviceModel,
                    "manufacturer" to feedback.manufacturer,
                    "language" to feedback.language,
                    "theme" to feedback.theme,
                    "totalScans" to feedback.totalScans,
                    "appOpenCount" to feedback.appOpenCount,
                    "createdAt" to FieldValue.serverTimestamp()
                )
                
                firestore.collection(collectionName)
                    .document(feedback.feedbackId)
                    .set(data)
                    .await()
                
                feedbackDao.deleteFeedback(feedback.feedbackId)
                Log.d("FeedbackSyncWorker", "Successfully synced feedback: ${feedback.feedbackId}")
            } catch (e: Exception) {
                Log.e("FeedbackSyncWorker", "Failed to sync feedback: ${feedback.feedbackId}", e)
                allSuccessful = false
            }
        }

        return if (allSuccessful) Result.success() else Result.retry()
    }

    companion object {
        fun enqueue(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val syncWorkRequest = OneTimeWorkRequestBuilder<FeedbackSyncWorker>()
                .setConstraints(constraints)
                .build()

            WorkManager.getInstance(context).enqueueUniqueWork(
                "FeedbackSyncWorker",
                ExistingWorkPolicy.REPLACE,
                syncWorkRequest
            )
        }
    }
}
