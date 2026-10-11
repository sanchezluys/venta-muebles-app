package com.sanchezluys.ventamuebles.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sanchezluys.ventamuebles.model.Sale
import kotlinx.coroutines.flow.Flow

@Dao
interface SaleDao {

    @Query("SELECT * FROM sales ORDER BY timestamp DESC")
    fun getAllSales(): Flow<List<Sale>>

    @Query("SELECT * FROM sales WHERE timestamp >= :startTime AND timestamp <= :endTime ORDER BY timestamp DESC")
    fun getSalesBetween(startTime: Long, endTime: Long): Flow<List<Sale>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSale(sale: Sale): Long

    @Query("SELECT * FROM sales WHERE id = :id")
    suspend fun getSaleById(id: Long): Sale?

    @Query("DELETE FROM sales WHERE id = :id")
    suspend fun deleteSaleById(id: Long): Int

    @Query("SELECT COUNT(*) FROM sales WHERE timestamp >= :startTime AND timestamp <= :endTime")
    fun getSaleCountBetween(startTime: Long, endTime: Long): Flow<Int>

    @Query("SELECT COALESCE(SUM(totalPrice), 0.0) FROM sales WHERE timestamp >= :startTime AND timestamp <= :endTime")
    fun getTotalAmountBetween(startTime: Long, endTime: Long): Flow<Double>
}
