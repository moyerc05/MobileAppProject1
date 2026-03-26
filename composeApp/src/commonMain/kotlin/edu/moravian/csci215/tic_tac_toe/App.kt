package edu.moravian.csci215.tic_tac_toe

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import edu.moravian.csci215.tic_tac_toe.game.AppTheme
import edu.moravian.csci215.tic_tac_toe.game.Board.Companion.toStringRepresentation
import edu.moravian.csci215.tic_tac_toe.game.GameOverScreen
import edu.moravian.csci215.tic_tac_toe.game.GameScreen
import kotlinx.coroutines.launch
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

@Serializable
data class GameOverRoute(
    val winnerName: String?, // Null if it's a tie
    val p1Name: String,
    val p2Name: String,
    val p1Type: PlayerType,
    val p2Type: PlayerType,
    val boardSnapshot: String // Serialized board state
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App() {
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // Persistent Scores
    var p1Wins by remember { mutableIntStateOf(0) }
    var p2Wins by remember { mutableIntStateOf(0) }
    var ties by remember { mutableIntStateOf(0) }

    AppTheme {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                val backStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = backStackEntry?.destination

                // Show top bar ONLY when not on Welcome screen
                if (currentDestination?.hasRoute<WelcomeRoute>() != true) {
                    TopAppBar(
                        title = {
                            if (currentDestination?.hasRoute<GameOverRoute>() == true) {
                                Text("Game Over")
                            } else {
                                Text("Game")
                            }
                        },
                        navigationIcon = {
                            IconButton(onClick = {
                                // If on Game Over screen, act exactly like "Play Again"
                                if (currentDestination?.hasRoute<GameOverRoute>() == true) {
                                    val route = backStackEntry?.toRoute<GameOverRoute>()
                                    if (route != null) {
                                        navController.navigate(
                                            GameRoute(
                                                p1Name = route.p1Name,
                                                p2Name = route.p2Name,
                                                p1Type = route.p1Type,
                                                p2Type = route.p2Type
                                            )
                                        ) {
                                            popUpTo<GameOverRoute> { inclusive = true }
                                        }
                                    }
                                } else {
                                    // Normal back button behavior (e.g., leaving an active game)
                                    navController.navigateUp()
                                }
                            }) {
                                Text("<") // simple back arrow
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

                // WELCOME SCREEN
                composable<WelcomeRoute> {
                    LaunchedEffect(Unit) {
                        p1Wins = 0
                        p2Wins = 0
                        ties = 0
                    }
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

                // GAME SCREEN
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
                        },
                        onGameOver = { winnerPiece, finalBoard ->
                            // Determine winner and update scores
                            val winnerName = when (winnerPiece) {
                                'X' -> {
                                    p1Wins++
                                    route.p1Name
                                }
                                'O' -> {
                                    p2Wins++
                                    route.p2Name
                                }
                                else -> {
                                    ties++
                                    null
                                }
                            }

                            // Navigate to Game Over and pop the finished GameRoute off the stack
                            navController.navigate(
                                GameOverRoute(
                                    winnerName = winnerName,
                                    p1Name = route.p1Name,
                                    p2Name = route.p2Name,
                                    p1Type = route.p1Type,
                                    p2Type = route.p2Type,
                                    boardSnapshot = finalBoard.toStringRepresentation()
                                )
                            ) {
                                popUpTo<GameRoute> { inclusive = true }
                            }
                        }
                    )
                }

                // GAME OVER SCREEN
                composable<GameOverRoute> { backStackEntry ->
                    val route = backStackEntry.toRoute<GameOverRoute>()

                    GameOverScreen(
                        route = route,
                        p1Wins = p1Wins,
                        p2Wins = p2Wins,
                        ties = ties,
                        onPlayAgain = {
                            // Navigate to a fresh GameScreen and pop the GameOverScreen off the stack
                            navController.navigate(
                                GameRoute(
                                    p1Name = route.p1Name,
                                    p2Name = route.p2Name,
                                    p1Type = route.p1Type,
                                    p2Type = route.p2Type
                                )
                            ) {
                                popUpTo<GameOverRoute> { inclusive = true }
                            }
                        }
                    )
                }
            }
        }
    }
}