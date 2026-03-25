package edu.moravian.csci215.tic_tac_toe
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import edu.moravian.csci215.tic_tac_toe.game.AppTheme
import kotlinx.serialization.Serializable

@Serializable
object WelcomeRoute

@Serializable
data class GameRoute(
    val p1Name: String,
    val p2Name: String,
    val p1Type: PlayerType,
    val p2Type: PlayerType
)
@Composable
fun App() {
    AppTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background,
        ) {
            val navController = rememberNavController()

            // Use the Route object for the start destination
            NavHost(navController = navController, startDestination = WelcomeRoute) {

                // The Welcome Screen
                composable<WelcomeRoute> {
                    WelcomeScreen(
                        onStartGame = { p1Name, p2Name, p1Type, p2Type ->
                            navController.navigate(GameRoute(p1Name, p2Name, p1Type, p2Type))
                        },
                    )
                }

                // The Game Screen
                composable<GameRoute> { backStackEntry ->
                    val route = backStackEntry.toRoute<GameRoute>()

                    GameScreen(
                        p1Name = route.p1Name,
                        p2Name = route.p2Name,
                        p1Type = route.p1Type,
                        p2Type = route.p2Type,
                        onNavigateBack = { navController.popBackStack() },
                    )
                }
            }
        }
    }
}
