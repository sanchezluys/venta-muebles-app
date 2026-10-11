package com.sanchezluys.ventamuebles.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sales")
data class Sale(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val productName: String,
    val quantity: Int,
    val unitPrice: Double,
    val totalPrice: Double,
    val notes: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
