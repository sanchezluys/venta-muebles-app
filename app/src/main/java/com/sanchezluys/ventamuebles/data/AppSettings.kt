package com.sanchezluys.ventamuebles.data

import android.content.Context
import android.content.SharedPreferences

class AppSettings(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("ventas_muebles_prefs", Context.MODE_PRIVATE)

    var storeName: String
        get() = prefs.getString(KEY_STORE_NAME, "Local Central de Muebles") ?: "Local Central de Muebles"
        set(value) = prefs.edit().putString(KEY_STORE_NAME, value.trim()).apply()

    var sellerName: String
        get() = prefs.getString(KEY_SELLER_NAME, "Vendedor de Salón") ?: "Vendedor de Salón"
        set(value) = prefs.edit().putString(KEY_SELLER_NAME, value.trim()).apply()

    var ownerPhone: String
        get() = prefs.getString(KEY_OWNER_PHONE, "") ?: ""
        set(value) = prefs.edit().putString(KEY_OWNER_PHONE, value.trim()).apply()

    companion object {
        private const val KEY_STORE_NAME = "store_name"
        private const val KEY_SELLER_NAME = "seller_name"
        private const val KEY_OWNER_PHONE = "owner_phone"
    }
}
