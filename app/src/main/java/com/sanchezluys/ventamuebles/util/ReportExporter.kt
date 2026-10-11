package com.sanchezluys.ventamuebles.util

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import com.sanchezluys.ventamuebles.R
import com.sanchezluys.ventamuebles.model.Sale
import java.net.URLEncoder

object ReportExporter {

    private const val WHATSAPP_PACKAGE = "com.whatsapp"
    private const val WHATSAPP_BUSINESS_PACKAGE = "com.whatsapp.w4b"

    fun buildReportText(
        sales: List<Sale>,
        periodTitle: String,
        storeName: String,
        sellerName: String
    ): String {
        val totalAmount = sales.sumOf { it.totalPrice }
        val totalTransactions = sales.size
        val totalUnits = sales.sumOf { it.quantity }

        val sb = StringBuilder()
        sb.append("📋 *REPORTE DE VENTAS - MUEBLES*\n")
        sb.append("🏪 *Local:* ").append(storeName).append("\n")
        sb.append("👤 *Vendedor:* ").append(sellerName).append("\n")
        sb.append("📅 *Período:* ").append(periodTitle).append("\n")
        sb.append("⏰ *Generado:* ").append(DateUtils.formatDateTime(System.currentTimeMillis())).append("\n")
        sb.append("━━━━━━━━━━━━━━━━━━━━\n")
        sb.append("💰 *TOTAL RECAUDADO:* ").append(DateUtils.formatCurrency(totalAmount)).append("\n")
        sb.append("🧾 *TRANSACCIONES:* ").append(totalTransactions).append("\n")
        sb.append("📦 *UNIDADES VENDIDAS:* ").append(totalUnits).append("\n")
        sb.append("━━━━━━━━━━━━━━━━━━━━\n")

        if (sales.isEmpty()) {
            sb.append("*(Sin transacciones registradas en este período)*\n")
        } else {
            sb.append("📝 *DETALLE DE VENTAS:*\n")
            sales.forEachIndexed { index, sale ->
                sb.append("${index + 1}. *${sale.productName.trim()}*\n")
                sb.append("   • Cantidad: ${sale.quantity} und | Unit: ${DateUtils.formatCurrency(sale.unitPrice)}\n")
                sb.append("   • Total: *${DateUtils.formatCurrency(sale.totalPrice)}*")
                sb.append(" (Hora: ${DateUtils.formatTime(sale.timestamp)})\n")
                if (sale.notes.isNotBlank()) {
                    sb.append("   • Obs: ${sale.notes.trim()}\n")
                }
            }
        }
        sb.append("━━━━━━━━━━━━━━━━━━━━\n")
        sb.append("✅ _Reporte generado localmente en dispositivo de salón._")

        return sb.toString()
    }

    fun isWhatsAppInstalled(context: Context): Boolean {
        val pm = context.packageManager
        return isPackageInstalled(pm, WHATSAPP_PACKAGE) || isPackageInstalled(pm, WHATSAPP_BUSINESS_PACKAGE)
    }

    private fun isPackageInstalled(pm: PackageManager, packageName: String): Boolean {
        return try {
            pm.getPackageInfo(packageName, 0)
            true
        } catch (_: PackageManager.NameNotFoundException) {
            false
        }
    }

    fun shareReport(
        context: Context,
        reportText: String,
        targetWhatsApp: Boolean = false,
        phoneTarget: String? = null
    ) {
        if (targetWhatsApp) {
            val cleanPhone = phoneTarget?.replace(Regex("[^0-9]"), "") ?: ""
            if (cleanPhone.isNotBlank()) {
                // Open WhatsApp chat directly with prefilled text so user only needs to press send
                try {
                    val encodedText = URLEncoder.encode(reportText, "UTF-8")
                    val uri = Uri.parse("https://api.whatsapp.com/send?phone=$cleanPhone&text=$encodedText")
                    val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    context.startActivity(intent)
                    return
                } catch (e: Exception) {
                    Toast.makeText(context, "No fue posible abrir WhatsApp directamente. Abriendo selector general.", Toast.LENGTH_SHORT).show()
                }
            } else if (isWhatsAppInstalled(context)) {
                // WhatsApp is installed, launch Intent targeting WhatsApp
                val whatsappIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, reportText)
                    val target = if (isPackageInstalled(context.packageManager, WHATSAPP_PACKAGE)) {
                        WHATSAPP_PACKAGE
                    } else {
                        WHATSAPP_BUSINESS_PACKAGE
                    }
                    setPackage(target)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                try {
                    context.startActivity(whatsappIntent)
                    return
                } catch (_: Exception) {
                    // Fallback to standard chooser
                }
            }
        }

        // Standard Android Intent.ACTION_SEND with chooser
        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "Reporte de Ventas de Muebles")
            putExtra(Intent.EXTRA_TEXT, reportText)
        }
        val chooserIntent = Intent.createChooser(sendIntent, context.getString(R.string.share_chooser_title)).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(chooserIntent)
    }
}
