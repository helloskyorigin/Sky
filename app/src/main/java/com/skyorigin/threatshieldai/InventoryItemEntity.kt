package com.skyorigin.threatshieldai

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "inventory_items")
data class InventoryItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val sku: String,
    val quantity: Int,
    val price: Double,
    val category: String,
    val description: String,
    val lastUpdated: Long = System.currentTimeMillis()
)
