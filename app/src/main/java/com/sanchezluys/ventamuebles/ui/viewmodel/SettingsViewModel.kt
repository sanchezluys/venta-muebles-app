package com.sanchezluys.ventamuebles.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sanchezluys.ventamuebles.data.AppSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class SettingsUiState(
    val storeName: String = "",
    val sellerName: String = "",
    val ownerPhone: String = "",
    val isSavedSuccess: Boolean = false
)

class SettingsViewModel(private val settings: AppSettings) : ViewModel() {

    private val _uiState = MutableStateFlow(
        SettingsUiState(
            storeName = settings.storeName,
            sellerName = settings.sellerName,
            ownerPhone = settings.ownerPhone
        )
    )
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    fun updateStoreName(value: String) {
        _uiState.update { it.copy(storeName = value, isSavedSuccess = false) }
    }

    fun updateSellerName(value: String) {
        _uiState.update { it.copy(sellerName = value, isSavedSuccess = false) }
    }

    fun updateOwnerPhone(value: String) {
        _uiState.update { it.copy(ownerPhone = value, isSavedSuccess = false) }
    }

    fun saveSettings() {
        val current = _uiState.value
        settings.storeName = current.storeName
        settings.sellerName = current.sellerName
        settings.ownerPhone = current.ownerPhone
        _uiState.update { it.copy(isSavedSuccess = true) }
    }

    fun clearSavedSuccess() {
        _uiState.update { it.copy(isSavedSuccess = false) }
    }

    class Factory(private val settings: AppSettings) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return SettingsViewModel(settings) as T
        }
    }
}
