package com.sanchezluys.ventamuebles.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.sanchezluys.ventamuebles.data.AppSettings
import com.sanchezluys.ventamuebles.data.SaleRepository
import com.sanchezluys.ventamuebles.model.Sale
import com.sanchezluys.ventamuebles.util.DateUtils
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class PeriodFilter(val label: String) {
    TODAY("Hoy"),
    YESTERDAY("Ayer"),
    THIS_WEEK("Esta Semana"),
    THIS_MONTH("Este Mes"),
    ALL("Todas")
}

data class ReportsUiState(
    val selectedPeriod: PeriodFilter = PeriodFilter.TODAY,
    val sales: List<Sale> = emptyList(),
    val totalAmount: Double = 0.0,
    val transactionCount: Int = 0,
    val totalUnits: Int = 0,
    val averageTicket: Double = 0.0,
    val periodTitle: String = "Hoy",
    val storeName: String = "",
    val sellerName: String = "",
    val ownerPhone: String = ""
)

@OptIn(ExperimentalCoroutinesApi::class)
class ReportsViewModel(
    private val repository: SaleRepository,
    private val settings: AppSettings
) : ViewModel() {

    private val _selectedPeriod = MutableStateFlow(PeriodFilter.TODAY)
    val selectedPeriod: StateFlow<PeriodFilter> = _selectedPeriod.asStateFlow()

    val uiState: StateFlow<ReportsUiState> = _selectedPeriod.flatMapLatest { period ->
        when (period) {
            PeriodFilter.TODAY -> {
                val start = DateUtils.getStartOfDay()
                val end = DateUtils.getEndOfDay()
                repository.getSalesBetween(start, end).map { list ->
                    createState(period, "Hoy (${DateUtils.formatDate(System.currentTimeMillis())})", list)
                }
            }
            PeriodFilter.YESTERDAY -> {
                val (start, end) = DateUtils.getYesterdayStartAndEnd()
                repository.getSalesBetween(start, end).map { list ->
                    createState(period, "Ayer (${DateUtils.formatShortDate(start)})", list)
                }
            }
            PeriodFilter.THIS_WEEK -> {
                val (start, end) = DateUtils.getThisWeekStartAndEnd()
                repository.getSalesBetween(start, end).map { list ->
                    createState(period, "Esta Semana (${DateUtils.formatShortDate(start)} al ${DateUtils.formatShortDate(end)})", list)
                }
            }
            PeriodFilter.THIS_MONTH -> {
                val (start, end) = DateUtils.getThisMonthStartAndEnd()
                repository.getSalesBetween(start, end).map { list ->
                    createState(period, "Este Mes (${DateUtils.formatShortDate(start)} al ${DateUtils.formatShortDate(end)})", list)
                }
            }
            PeriodFilter.ALL -> {
                repository.allSales.map { list ->
                    createState(period, "Historial Completo", list)
                }
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ReportsUiState(
            storeName = settings.storeName,
            sellerName = settings.sellerName,
            ownerPhone = settings.ownerPhone
        )
    )

    private fun createState(period: PeriodFilter, periodTitle: String, sales: List<Sale>): ReportsUiState {
        val total = sales.sumOf { it.totalPrice }
        val count = sales.size
        val units = sales.sumOf { it.quantity }
        val avg = if (count > 0) total / count else 0.0
        return ReportsUiState(
            selectedPeriod = period,
            sales = sales,
            totalAmount = total,
            transactionCount = count,
            totalUnits = units,
            averageTicket = avg,
            periodTitle = periodTitle,
            storeName = settings.storeName,
            sellerName = settings.sellerName,
            ownerPhone = settings.ownerPhone
        )
    }

    fun setPeriod(period: PeriodFilter) {
        _selectedPeriod.value = period
    }

    fun deleteSale(saleId: Long) {
        viewModelScope.launch {
            repository.deleteById(saleId)
        }
    }

    class Factory(
        private val repository: SaleRepository,
        private val settings: AppSettings
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ReportsViewModel(repository, settings) as T
        }
    }
}
