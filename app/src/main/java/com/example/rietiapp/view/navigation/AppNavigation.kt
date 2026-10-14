package com.example.rietiapp.view.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.rietiapp.view.screens.formularioPantallas.ConfirmacionScreen
import com.example.rietiapp.view.screens.formularioPantallas.FormularioScreen
import com.example.rietiapp.view.screens.formularioPantallas.TipoReporteScreen
import com.example.rietiapp.view.screens.pantallaPrincipal.HomeScreen
import com.example.rietiapp.view.screens.pantallaPrincipal.PeligroScreen
import com.example.rietiapp.view.screens.seguimientoReportes.ConsultaReporteScreen
import com.example.rietiapp.view.screens.seguimientoReportes.SeguimientoScreen
import com.example.rietiapp.viewmodel.ReporteViewModel
import com.example.rietiapp.viewmodel.SeguimientoViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    val reporteViewModel: ReporteViewModel = viewModel()
    val seguimientoViewModel: SeguimientoViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = Routes.HOME,
        modifier = Modifier
    ) {
        composable(Routes.HOME) {
            HomeScreen(
                onReportarClick = {
                    reporteViewModel.updateState { com.example.rietiapp.model.datos.ReporteUiState() }
                    navController.navigate(Routes.TIPO_REPORTE)
                },
                onSeguimientoClick = {
                    navController.navigate(Routes.SEGUIMIENTO)
                }
            )
        }

        composable(Routes.TIPO_REPORTE) {
            TipoReporteScreen(
                viewModel = reporteViewModel,
                onContinuarClick = {
                    navController.navigate(Routes.FORMULARIO)
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(Routes.FORMULARIO) {
            FormularioScreen(
                viewModel = reporteViewModel,
                onEnviarClick = {
                    navController.navigate(Routes.CONFIRMACION) {
                        popUpTo(Routes.HOME)
                    }
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(Routes.CONFIRMACION) {
            ConfirmacionScreen(
                viewModel = reporteViewModel,
                onInicioClick = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.HOME) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.SEGUIMIENTO) {
            SeguimientoScreen(
                viewModel = seguimientoViewModel,
                onConsultarClick = {
                    navController.navigate(Routes.CONSULTA)
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(Routes.CONSULTA) {
            ConsultaReporteScreen(
                viewModel = seguimientoViewModel,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(Routes.PELIGRO) {
            PeligroScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}