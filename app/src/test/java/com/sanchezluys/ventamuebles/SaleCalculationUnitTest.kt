package com.sanchezluys.ventamuebles

import com.sanchezluys.ventamuebles.model.Sale
import com.sanchezluys.ventamuebles.util.DateUtils
import com.sanchezluys.ventamuebles.util.ReportExporter
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SaleCalculationUnitTest {

    @Test
    fun testRetailSaleTotalCalculation() {
        val quantity = 3
        val unitPrice = 450000.0
        val total = quantity * unitPrice
        val sale = Sale(
            productName = "Comedor Roble 6 puestos",
            quantity = quantity,
            unitPrice = unitPrice,
            totalPrice = total
        )

        assertEquals(1350000.0, sale.totalPrice, 0.001)
        assertEquals(3, sale.quantity)
        assertEquals("Comedor Roble 6 puestos", sale.productName)
    }

    @Test
    fun testReportTextFormatting() {
        val sales = listOf(
            Sale(
                id = 1,
                productName = "Sillón Astrid",
                quantity = 2,
                unitPrice = 300000.0,
                totalPrice = 600000.0,
                timestamp = System.currentTimeMillis()
            )
        )

        val report = ReportExporter.buildReportText(
            sales = sales,
            periodTitle = "Hoy",
            storeName = "Mueblería Central",
            sellerName = "Luis Sánchez"
        )

        assertTrue(report.contains("REPORTE DE VENTAS - MUEBLES"))
        assertTrue(report.contains("Mueblería Central"))
        assertTrue(report.contains("Luis Sánchez"))
        assertTrue(report.contains("Sillón Astrid"))
        assertTrue(report.contains("TRANSACCIONES:* 1"))
    }
}
