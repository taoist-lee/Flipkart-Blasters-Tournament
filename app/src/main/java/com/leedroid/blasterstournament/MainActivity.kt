package com.leedroid.blasterstournament

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.leedroid.blasterstournament.ui.points.PointsTableRoute
import com.leedroid.blasterstournament.ui.points.PointsTableViewModel
import com.leedroid.blasterstournament.ui.matches.PlayerMatchesRoute
import com.leedroid.blasterstournament.ui.matches.PlayerMatchesViewModel
import com.leedroid.blasterstournament.ui.theme.BlastersTournamentTheme
import dagger.hilt.android.AndroidEntryPoint
import androidx.hilt.navigation.compose.hiltViewModel

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { BlastersTournamentTheme { AppNavHost() } }
    }
}

private const val ROUTE_POINTS = "points"
private const val ROUTE_PLAYER_MATCHES = "playerMatches/{playerId}"

@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = ROUTE_POINTS) {
        composable(ROUTE_POINTS) {
            val vm: PointsTableViewModel = hiltViewModel()
            PointsTableRoute(
                viewModel = vm,
                onPlayerClick = { id -> navController.navigate("playerMatches/$id") })
        }
        composable(
            ROUTE_PLAYER_MATCHES,
            arguments = listOf(navArgument("playerId") { type = NavType.IntType })
        ) { backStackEntry ->
            val playerId = backStackEntry.arguments?.getInt("playerId") ?: return@composable
            val vm: PlayerMatchesViewModel = hiltViewModel()
            PlayerMatchesRoute(
                playerId = playerId,
                viewModel = vm,
                onBack = { navController.popBackStack() })
        }
    }
}