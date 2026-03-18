package edu.moravian.csci215.tic_tac_toe
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import edu.moravian.csci215.tic_tac_toe.game.AppTheme

@Composable
fun App() {
    AppTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background,
        ) {
            val navController = rememberNavController()
            NavHost(navController = navController, startDestination = "welcome") {
                // The Welcome Screen
                composable("welcome") {
                    WelcomeScreen(
                        onStartGame = { p1Name, p2Name, p1Type, p2Type ->
                            // When the user clicks Start, navigate to the game route
                            // We convert the Enums to Strings using .name so they fit in the route
                            navController.navigate("game/$p1Name/$p2Name/${p1Type.name}/${p2Type.name}")
                        },
                    )
                }

                composable("game/{p1Name}/{p2Name}/{p1Type}/{p2Type}") { backStackEntry ->

                    // Extract the strings from the navigation route
                    val p1Name = backStackEntry.arguments?.getString("p1Name") ?: "Player 1"
                    val p2Name = backStackEntry.arguments?.getString("p2Name") ?: "Player 2"
                    val p1TypeString = backStackEntry.arguments?.getString("p1Type") ?: "HUMAN"
                    val p2TypeString = backStackEntry.arguments?.getString("p2Type") ?: "HUMAN"

                    // Convert the strings back into PlayerType Enums
                    val p1Type = PlayerType.valueOf(p1TypeString)
                    val p2Type = PlayerType.valueOf(p2TypeString)

                    // Pass the extracted data into the actual GameScreen
                    GameScreen(
                        p1Name,
                        p2Name,
                        p1Type,
                        p2Type,
                        onNavigateBack = { navController.popBackStack() },
                    )
                }
            }
        }
    }
}
