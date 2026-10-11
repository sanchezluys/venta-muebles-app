package com.sanchezluys.ventamuebles

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.sanchezluys.ventamuebles.data.AppDatabase
import com.sanchezluys.ventamuebles.model.Sale
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class VentaMueblesRobolectricTest {

    private lateinit var database: AppDatabase

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun readStringResourceFromContext() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Reporte Ventas Muebles", appName)
    }

    @Test
    fun insertAndQuerySaleInRoomDatabase() = runBlocking {
        val dao = database.saleDao()
        val sale = Sale(
            productName = "Mesa de Centro Nogal",
            quantity = 1,
            unitPrice = 280000.0,
            totalPrice = 280000.0,
            timestamp = System.currentTimeMillis()
        )

        val id = dao.insertSale(sale)
        val loaded = dao.getSaleById(id)

        assertNotNull(loaded)
        assertEquals("Mesa de Centro Nogal", loaded?.productName)
        assertEquals(280000.0, loaded?.totalPrice ?: 0.0, 0.001)

        val allSales = dao.getAllSales().first()
        assertEquals(1, allSales.size)
    }
}
