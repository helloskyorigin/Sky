package com.skyorigin.threatshieldai

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ScanHistoryDao {
    @Query("SELECT * FROM scan_history ORDER BY timestamp DESC")
    fun getAllHistory(): Flow<List<ScanHistoryEntity>>

    @Query("SELECT * FROM scan_history WHERE timestamp = :timestamp LIMIT 1")
    suspend fun getScanByTimestamp(timestamp: Long): ScanHistoryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScan(scan: ScanHistoryEntity)

    @Query("DELETE FROM scan_history WHERE text = :text")
    suspend fun deleteScanByText(text: String)

    @Query("DELETE FROM scan_history")
    suspend fun clearAll()
}
