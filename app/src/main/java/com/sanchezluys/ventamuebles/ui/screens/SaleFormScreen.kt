package com.sanchezluys.ventamuebles.ui.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sanchezluys.ventamuebles.R
import com.sanchezluys.ventamuebles.ui.components.OfflineStatusBadge
import com.sanchezluys.ventamuebles.ui.viewmodel.SaleFormEvent
import com.sanchezluys.ventamuebles.ui.viewmodel.SaleFormViewModel
import com.sanchezluys.ventamuebles.util.DateUtils
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SaleFormScreen(
    viewModel: SaleFormViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val formState by viewModel.formState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()

    BackHandler {
        onNavigateBack()
    }

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is SaleFormEvent.Success -> {
                    Toast.makeText(
                        context,
                        "¡Venta de «${event.productName}» registrada por ${DateUtils.formatCurrency(event.total)}!",
                        Toast.LENGTH_LONG
                    ).show()
                    onNavigateBack()
                }
                is SaleFormEvent.Error -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("sale_form_screen"),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = stringResource(R.string.sale_form_title),
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = stringResource(R.string.sale_form_subtitle),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("btn_back_form")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                actions = {
                    OfflineStatusBadge(modifier = Modifier.padding(end = 8.dp))
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Tarjeta de Cálculo Automático del Total en Tiempo Real
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("calculated_total_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "TOTAL CALCULADO",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                            )
                        )
                        Text(
                            text = DateUtils.formatCurrency(formState.calculatedTotal),
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            ),
                            modifier = Modifier.testTag("calculated_total_display")
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.primary
                    ) {
                        Text(
                            text = "${formState.quantity} unds",
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        )
                    }
                }
            }

            // Campo: Nombre del Mueble / Producto
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = formState.productName,
                    onValueChange = { viewModel.onProductChanged(it) },
                    label = { Text("Producto / Mueble *") },
                    placeholder = { Text("Ej. Comedor Roble 6 puestos") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Inventory, contentDescription = null)
                    },
                    isError = formState.productError != null,
                    supportingText = {
                        if (formState.productError != null) {
                            Text(
                                text = formState.productError ?: "",
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.testTag("error_product_text")
                            )
                        } else {
                            Text("Nombre comercial del mueble vendido")
                        }
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_product_name")
                )

                // Chips de sugerencia rápida de muebles para piso de venta
                Text(
                    text = "Sugerencias rápidas de salón:",
                    style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                    modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    viewModel.popularFurnitureSuggestions.forEach { suggestion ->
                        FilterChip(
                            selected = formState.productName == suggestion,
                            onClick = { viewModel.onProductChanged(suggestion) },
                            label = { Text(suggestion, fontSize = 12.sp) },
                            modifier = Modifier.testTag("chip_suggestion_${suggestion.take(8)}")
                        )
                    }
                }
            }

            // Selector de Cantidad con Stepper grande (+ / -) y teclado
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Cantidad de Unidades *",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        FilledTonalIconButton(
                            onClick = { viewModel.onQuantityChanged(formState.quantity - 1) },
                            enabled = formState.quantity > 1,
                            modifier = Modifier
                                .size(48.dp)
                                .testTag("btn_quantity_minus")
                        ) {
                            Icon(imageVector = Icons.Default.Remove, contentDescription = "Menos")
                        }

                        Text(
                            text = "${formState.quantity}",
                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
                            modifier = Modifier.testTag("quantity_display_text")
                        )

                        FilledTonalIconButton(
                            onClick = { viewModel.onQuantityChanged(formState.quantity + 1) },
                            modifier = Modifier
                                .size(48.dp)
                                .testTag("btn_quantity_plus")
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = "Más")
                        }
                    }

                    if (formState.quantityError != null) {
                        Text(
                            text = formState.quantityError ?: "",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }

            // Campo: Precio Unitario Minorista
            OutlinedTextField(
                value = formState.unitPriceInput,
                onValueChange = { viewModel.onUnitPriceInputChanged(it) },
                label = { Text("Precio Minorista Unitario ($) *") },
                placeholder = { Text("Ej. 450000") },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.AttachMoney, contentDescription = null)
                },
                isError = formState.priceError != null,
                supportingText = {
                    if (formState.priceError != null) {
                        Text(
                            text = formState.priceError ?: "",
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.testTag("error_price_text")
                        )
                    } else {
                        Text("Precio final de venta (sin descuentos mayoristas ni créditos)")
                    }
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { focusManager.clearFocus() }
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_unit_price")
            )

            // Campo: Notas / Observaciones
            OutlinedTextField(
                value = formState.notes,
                onValueChange = { viewModel.onNotesChanged(it) },
                label = { Text(stringResource(R.string.field_notes)) },
                placeholder = { Text(stringResource(R.string.field_notes_hint)) },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Notes, contentDescription = null)
                },
                maxLines = 3,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_notes")
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Botón Grande: Guardar Venta
            Button(
                onClick = {
                    focusManager.clearFocus()
                    viewModel.saveSale()
                },
                enabled = !formState.isSaving,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp)
                    .testTag("btn_save_sale"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 3.dp)
            ) {
                if (formState.isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(text = stringResource(R.string.saving_sale))
                } else {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "GUARDAR VENTA MINORISTA",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                    )
                }
            }
        }
    }
}
