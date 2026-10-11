package com.sanchezluys.ventamuebles.data

import com.sanchezluys.ventamuebles.model.Sale
import kotlinx.coroutines.flow.Flow

class SaleRepository(private val saleDao: SaleDao) {

    val allSales: Flow<List<Sale>> = saleDao.getAllSales()

    fun getSalesBetween(startTime: Long, endTime: Long): Flow<List<Sale>> {
        return saleDao.getSalesBetween(startTime, endTime)
    }

    fun getSaleCountBetween(startTime: Long, endTime: Long): Flow<Int> {
        return saleDao.getSaleCountBetween(startTime, endTime)
    }

    fun getTotalAmountBetween(startTime: Long, endTime: Long): Flow<Double> {
        return saleDao.getTotalAmountBetween(startTime, endTime)
    }

    suspend fun insert(sale: Sale): Long {
        return saleDao.insertSale(sale)
    }

    suspend fun getSaleById(id: Long): Sale? {
        return saleDao.getSaleById(id)
    }

    suspend fun deleteById(id: Long): Int {
        return saleDao.deleteSaleById(id)
    }
}
