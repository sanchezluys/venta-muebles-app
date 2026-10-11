package com.sanchezluys.ventamuebles.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.sanchezluys.ventamuebles.data.AppSettings
import com.sanchezluys.ventamuebles.data.SaleRepository
import com.sanchezluys.ventamuebles.model.Sale
import com.sanchezluys.ventamuebles.util.DateUtils
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class DashboardUiState(
    val todaySales: List<Sale> = emptyList(),
    val todayTotal: Double = 0.0,
    val todayTransactions: Int = 0,
    val todayUnits: Int = 0,
    val todayAvgTicket: Double = 0.0,
    val storeName: String = "",
    val sellerName: String = "",
    val ownerPhone: String = ""
)

class DashboardViewModel(
    private val repository: SaleRepository,
    private val settings: AppSettings
) : ViewModel() {

    private val todayStart = DateUtils.getStartOfDay()
    private val todayEnd = DateUtils.getEndOfDay()

    val uiState: StateFlow<DashboardUiState> = repository.getSalesBetween(todayStart, todayEnd)
        .map { sales ->
            val total = sales.sumOf { it.totalPrice }
            val count = sales.size
            val units = sales.sumOf { it.quantity }
            val avg = if (count > 0) total / count else 0.0
            DashboardUiState(
                todaySales = sales,
                todayTotal = total,
                todayTransactions = count,
                todayUnits = units,
                todayAvgTicket = avg,
                storeName = settings.storeName,
                sellerName = settings.sellerName,
                ownerPhone = settings.ownerPhone
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = DashboardUiState(
                storeName = settings.storeName,
                sellerName = settings.sellerName,
                ownerPhone = settings.ownerPhone
            )
        )

    class Factory(
        private val repository: SaleRepository,
        private val settings: AppSettings
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return DashboardViewModel(repository, settings) as T
        }
    }
}
