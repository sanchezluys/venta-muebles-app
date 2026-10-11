package com.sanchezluys.ventamuebles.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.sanchezluys.ventamuebles.data.SaleRepository
import com.sanchezluys.ventamuebles.model.Sale
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SaleFormState(
    val productName: String = "",
    val quantity: Int = 1,
    val unitPriceInput: String = "",
    val notes: String = "",
    val productError: String? = null,
    val quantityError: String? = null,
    val priceError: String? = null,
    val isSaving: Boolean = false,
    val calculatedTotal: Double = 0.0
)

sealed interface SaleFormEvent {
    data class Success(val saleId: Long, val productName: String, val total: Double) : SaleFormEvent
    data class Error(val message: String) : SaleFormEvent
}

class SaleFormViewModel(private val repository: SaleRepository) : ViewModel() {

    private val _formState = MutableStateFlow(SaleFormState())
    val formState: StateFlow<SaleFormState> = _formState.asStateFlow()

    private val _events = MutableSharedFlow<SaleFormEvent>()
    val events: SharedFlow<SaleFormEvent> = _events.asSharedFlow()

    // Prevenir dobles registros accidentales por pulsaciones múltiples
    private var lastSavedTimestamp: Long = 0L

    val popularFurnitureSuggestions = listOf(
        "Comedor Roble 6 puestos",
        "Sillón Astrid Nórdico",
        "Mesa de Centro Nogal",
        "Cama King Tapizada",
        "Poltrona Sala Roble",
        "Aparador 3 Puertas",
        "Escritorio Ejecutivo Madera",
        "Juego de Alcoba Completo"
    )

    fun onProductChanged(name: String) {
        _formState.update { current ->
            current.copy(
                productName = name,
                productError = if (name.isNotBlank()) null else current.productError
            )
        }
    }

    fun onQuantityChanged(qty: Int) {
        if (qty < 1) return
        _formState.update { current ->
            val price = current.unitPriceInput.toDoubleOrNull() ?: 0.0
            current.copy(
                quantity = qty,
                quantityError = null,
                calculatedTotal = qty * price
            )
        }
    }

    fun onUnitPriceInputChanged(input: String) {
        // Allow only digits and optional single decimal point
        val clean = input.filter { it.isDigit() || it == '.' }
        if (clean.count { it == '.' } > 1) return

        _formState.update { current ->
            val price = clean.toDoubleOrNull() ?: 0.0
            current.copy(
                unitPriceInput = clean,
                priceError = if (price > 0) null else current.priceError,
                calculatedTotal = current.quantity * price
            )
        }
    }

    fun onNotesChanged(notes: String) {
        _formState.update { it.copy(notes = notes) }
    }

    fun resetForm() {
        _formState.update {
            SaleFormState(
                productName = "",
                quantity = 1,
                unitPriceInput = "",
                notes = "",
                calculatedTotal = 0.0
            )
        }
    }

    fun saveSale() {
        val current = _formState.value

        // Protección contra pulsaciones simultáneas / duplicadas
        if (current.isSaving) return

        val now = System.currentTimeMillis()
        if (now - lastSavedTimestamp < 1500) {
            // Evitar pulsaciones dobles casi instantáneas
            return
        }

        // Validaciones obligatorias
        val trimmedProduct = current.productName.trim()
        val price = current.unitPriceInput.toDoubleOrNull() ?: 0.0

        var hasError = false
        var productErr: String? = null
        var priceErr: String? = null
        var qtyErr: String? = null

        if (trimmedProduct.isBlank()) {
            productErr = "El nombre del producto es obligatorio."
            hasError = true
        }

        if (current.quantity <= 0) {
            qtyErr = "La cantidad debe ser mayor a 0."
            hasError = true
        }

        if (price <= 0.0) {
            priceErr = "El precio unitario debe ser mayor a $0."
            hasError = true
        }

        if (hasError) {
            _formState.update {
                it.copy(
                    productError = productErr,
                    quantityError = qtyErr,
                    priceError = priceErr
                )
            }
            return
        }

        _formState.update { it.copy(isSaving = true) }

        viewModelScope.launch {
            try {
                val total = current.quantity * price
                val sale = Sale(
                    productName = trimmedProduct,
                    quantity = current.quantity,
                    unitPrice = price,
                    totalPrice = total,
                    notes = current.notes.trim(),
                    timestamp = System.currentTimeMillis()
                )

                val id = repository.insert(sale)
                lastSavedTimestamp = System.currentTimeMillis()

                _events.emit(SaleFormEvent.Success(id, sale.productName, total))
                resetForm()
            } catch (e: Exception) {
                _events.emit(SaleFormEvent.Error(e.message ?: "Error al guardar la venta."))
            } finally {
                _formState.update { it.copy(isSaving = false) }
            }
        }
    }

    class Factory(private val repository: SaleRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return SaleFormViewModel(repository) as T
        }
    }
}
