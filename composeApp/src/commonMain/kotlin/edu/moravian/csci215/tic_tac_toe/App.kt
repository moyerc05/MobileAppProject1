package edu.moravian.csci215.tic_tac_toe
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import edu.moravian.csci215.tic_tac_toe.game.AppTheme
import edu.moravian.csci215.tic_tac_toe.game.GameScreen
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.compose.runtime.*

@Serializable
object WelcomeRoute

@Serializable
data class GameRoute(
    val p1Name: String,
    val p2Name: String,
    val p1Type: PlayerType,
    val p2Type: PlayerType
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App() {
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    AppTheme {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                val backStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = backStackEntry?.destination

                // Show top bar ONLY when not on Welcome screen
                if (currentDestination?.hasRoute<WelcomeRoute>() != true) {
                    TopAppBar(
                        title = { Text("Game") },
                        navigationIcon = {
                            IconButton(onClick = { navController.navigateUp() }) {
                                Text("<") // simple back arrow (replace later)
                            }
                        }
                    )
                }
            }
        ) { padding ->

            NavHost(
                navController = navController,
                startDestination = WelcomeRoute,
                modifier = Modifier.padding(padding)
            ) {

                composable<WelcomeRoute> {
                    WelcomeScreen(
                        onStartGame = { p1Name, p2Name, p1Type, p2Type ->
                            navController.navigate(
                                GameRoute(p1Name, p2Name, p1Type, p2Type)
                            )
                        },
                        showSnackbar = { message ->
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(message)
                            }
                        }
                    )
                }

                composable<GameRoute> { backStackEntry ->
                    val route = backStackEntry.toRoute<GameRoute>()

                    GameScreen(
                        p1Name = route.p1Name,
                        p2Name = route.p2Name,
                        p1Type = route.p1Type,
                        p2Type = route.p2Type,
                        showSnackbar = { message ->
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(message)
                            }
                        }
                    )
                }
            }
        }
    }
}
