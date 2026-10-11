package com.sanchezluys.ventamuebles

import android.app.Application
import com.sanchezluys.ventamuebles.data.AppDatabase
import com.sanchezluys.ventamuebles.data.AppSettings
import com.sanchezluys.ventamuebles.data.SaleRepository

class VentaMueblesApp : Application() {

    lateinit var database: AppDatabase
        private set

    lateinit var repository: SaleRepository
        private set

    lateinit var settings: AppSettings
        private set

    override fun onCreate() {
        super.onCreate()
        database = AppDatabase.getDatabase(this)
        repository = SaleRepository(database.saleDao())
        settings = AppSettings(this)
    }
}
