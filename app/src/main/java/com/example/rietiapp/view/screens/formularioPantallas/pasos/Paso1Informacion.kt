package com.example.rietiapp.view.screens.formularioPantallas.pasos

import android.app.TimePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

/**
 * Componente ejecutable del Paso 1 del formulario de reporte de incidencias.
 * Permite capturar los datos principales de la observación: cantidad de menores,
 * rango de edad, género, actividad realizada, hora aproximada, nivel de riesgo y descripción detallada.
 *
 * @param cantidadSeleccionada Valor seleccionado de cantidad de menores observados.
 * @param onCantidadChange Callback al cambiar la selección de cantidad.
 * @param edadSeleccionada Rango de edad seleccionado.
 * @param onEdadSelected Callback al seleccionar el rango de edad.
 * @param generoSeleccionado Género observado seleccionado.
 * @param onGeneroSelected Callback al seleccionar el género.
 * @param actividadSeleccionada Actividad identificada en el incidente.
 * @param onActividadSelected Callback al seleccionar la actividad.
 * @param hora Hora aproximada en formato `HH:mm`.
 * @param onHoraSelected Callback ejecutado cuando se selecciona una hora desde el [TimePickerDialog].
 * @param riesgo Indicador de presencia de situación de riesgo ("Sí", "No", "No se").
 * @param onRiesgoChange Callback al cambiar la opción de riesgo.
 * @param descripcion Texto descriptivo ingresado por el usuario sobre los hechos observados.
 * @param onDescripcionChange Callback para actualizar el texto de la descripción.
 * @param modifier Modificador composable opcional.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Paso1Informacion(
    cantidadSeleccionada: String,
    onCantidadChange: (String) -> Unit,
    edadSeleccionada: String,
    onEdadSelected: (String) -> Unit,
    generoSeleccionado: String,
    onGeneroSelected: (String) -> Unit,
    actividadSeleccionada: String,
    onActividadSelected: (String) -> Unit,
    hora: String,
    onHoraSelected: (String) -> Unit,
    riesgo: String,
    onRiesgoChange: (String) -> Unit,
    descripcion: String,
    onDescripcionChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    var expandedCantidad by remember { mutableStateOf(false) }
    var expandedEdad by remember { mutableStateOf(false) }
    var expandedGenero by remember { mutableStateOf(false) }
    var expandedActividad by remember { mutableStateOf(false) }

    val cantidades = listOf("1", "2", "3", "4", "5 o más", "No sé")
    val edades = listOf("0 a 5 años", "6 a 11 años", "12 a 17 años")
    val generos = listOf("Femenino", "Masculino", "No identificado")
    val actividades = listOf(
        "Mendicidad forzada",
        "Explotación sexual",
        "Trata de personas",
        "Utilización para actividades ilícitas",
        "Trabajo peligroso",
        "Otra situación de explotación y/o vulneración"
    )

    val maxCaracteres = 500

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "1. Cuéntanos lo que observaste",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Proporciona información aproximada. No es necesario que conozcas datos exactos.",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(24.dp))

            // --- 1. CANTIDAD DE NIÑOS ---
            Text(
                text = "¿Cuántas niñas, niños o adolescentes observaste? *",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            ExposedDropdownMenuBox(
                expanded = expandedCantidad,
                onExpandedChange = { expandedCantidad = !expandedCantidad }
            ) {
                OutlinedTextField(
                    value = cantidadSeleccionada.ifBlank { "Selecciona cantidad" },
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCantidad) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = expandedCantidad,
                    onDismissRequest = { expandedCantidad = false }
                ) {
                    cantidades.forEach { opcion ->
                        DropdownMenuItem(
                            text = { Text(opcion) },
                            onClick = {
                                onCantidadChange(opcion)
                                expandedCantidad = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- 2. EDAD APROXIMADA ---
            Text(
                text = "Edad aproximada *",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            ExposedDropdownMenuBox(
                expanded = expandedEdad,
                onExpandedChange = { expandedEdad = !expandedEdad }
            ) {
                OutlinedTextField(
                    value = edadSeleccionada.ifBlank { "Selecciona rango de edad" },
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedEdad) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = expandedEdad,
                    onDismissRequest = { expandedEdad = false }
                ) {
                    edades.forEach { edad ->
                        DropdownMenuItem(
                            text = { Text(edad) },
                            onClick = {
                                onEdadSelected(edad)
                                expandedEdad = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- 3. GÉNERO ---
            Text(
                text = "Género observado *",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            ExposedDropdownMenuBox(
                expanded = expandedGenero,
                onExpandedChange = { expandedGenero = !expandedGenero }
            ) {
                OutlinedTextField(
                    value = generoSeleccionado.ifBlank { "Selecciona género" },
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedGenero) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = expandedGenero,
                    onDismissRequest = { expandedGenero = false }
                ) {
                    generos.forEach { genero ->
                        DropdownMenuItem(
                            text = { Text(genero) },
                            onClick = {
                                onGeneroSelected(genero)
                                expandedGenero = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- 4. ACTIVIDAD OBSERVADA ---
            Text(
                text = "¿Qué actividad estaban realizando? *",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            ExposedDropdownMenuBox(
                expanded = expandedActividad,
                onExpandedChange = { expandedActividad = !expandedActividad }
            ) {
                OutlinedTextField(
                    value = actividadSeleccionada.ifBlank { "Selecciona actividad" },
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedActividad) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = expandedActividad,
                    onDismissRequest = { expandedActividad = false }
                ) {
                    actividades.forEach { actividad ->
                        DropdownMenuItem(
                            text = { Text(actividad) },
                            onClick = {
                                onActividadSelected(actividad)
                                expandedActividad = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- 5. HORA DE OBSERVACIÓN ---
            Text(
                text = "¿A qué hora aproximada observaste la situación?",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = hora.ifBlank { "Selecciona una hora" },
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clickable {
                            val timePicker = TimePickerDialog(
                                context,
                                { _, hour, minute ->
                                    onHoraSelected(String.format("%02d:%02d", hour, minute))
                                },
                                12,
                                0,
                                false
                            )
                            timePicker.show()
                        }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- 6. RIESGO ---
            Text(
                text = "¿Observaste alguna situación de riesgo?",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = riesgo == "Sí",
                        onClick = { onRiesgoChange("Sí") }
                    )
                    Text("Sí")
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = riesgo == "No",
                        onClick = { onRiesgoChange("No") }
                    )
                    Text("No")
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = riesgo == "No se",
                        onClick = { onRiesgoChange("No se") }
                    )
                    Text("No sé")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- 7. DESCRIPCIÓN ---
            Text(
                text = "Descripción de los hechos *",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = descripcion,
                onValueChange = { nuevoTexto ->
                    if (nuevoTexto.length <= maxCaracteres) {
                        onDescripcionChange(nuevoTexto)
                    }
                },
                placeholder = {
                    Text("Describe brevemente lo que observaste...")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp),
                singleLine = false,
                supportingText = {
                    Text(
                        text = "${descripcion.length}/$maxCaracteres",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = androidx.compose.ui.text.style.TextAlign.End,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            )
        }
    }
}
