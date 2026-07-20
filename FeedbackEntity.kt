package com.example

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "pending_feedback")
data class FeedbackEntity(
    @PrimaryKey val feedbackId: String,
    val rating: Int,
    val category: String?,
    val message: String?,
    val appVersion: String,
    val androidVersion: String,
    val deviceModel: String,
    val manufacturer: String,
    val language: String,
    val theme: String,
    val totalScans: Int,
    val appOpenCount: Int,
    val createdAt: Long
)

@Dao
interface FeedbackDao {
    @Query("SELECT * FROM pending_feedback")
    fun getAllPendingFeedbackFlow(): Flow<List<FeedbackEntity>>

    @Query("SELECT * FROM pending_feedback")
    suspend fun getAllPendingFeedback(): List<FeedbackEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFeedback(feedback: FeedbackEntity)

    @Query("DELETE FROM pending_feedback WHERE feedbackId = :feedbackId")
    suspend fun deleteFeedback(feedbackId: String)
}
