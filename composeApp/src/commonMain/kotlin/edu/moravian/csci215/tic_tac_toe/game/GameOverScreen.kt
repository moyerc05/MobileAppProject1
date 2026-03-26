package edu.moravian.csci215.tic_tac_toe.game

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.moravian.csci215.tic_tac_toe.GameOverRoute
import org.jetbrains.compose.resources.stringResource
import tictactoe.composeapp.generated.resources.Res
import tictactoe.composeapp.generated.resources.numTiesDisplay
import tictactoe.composeapp.generated.resources.playAgainButton

/**
 * Composable screen to display when a game is finished.
 *
 * Shows the winner (or tie), the final board state, and a scoreboard.
 *
 * @param route The GameOverRoute containing game result data.
 * @param p1Wins Number of wins for player 1.
 * @param p2Wins Number of wins for player 2.
 * @param ties Number of tied games.
 * @param onPlayAgain Callback invoked when the "Play Again" button is pressed.
 */
@Composable
fun GameOverScreen(
    route: GameOverRoute,
    p1Wins: Int,
    p2Wins: Int,
    ties: Int,
    onPlayAgain: () -> Unit,
) {
    val finalBoard = Board.createFromString(route.boardSnapshot)

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = if (route.winnerName != null) "${route.winnerName} Wins!" else "It's a Tie!",
            style = MaterialTheme.typography.displayMedium,
            color = MaterialTheme.colorScheme.primary,
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Show the final board (Non-clickable)
        Box(modifier = Modifier.size(200.dp)) {
            GameBoard(board = finalBoard, onCellClick = { _, _ -> })
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Scoreboard Table
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                ScoreRow(route.p1Name, p1Wins)
                ScoreRow(route.p2Name, p2Wins)
                ScoreRow(stringResource(Res.string.numTiesDisplay), ties)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onPlayAgain,
            modifier = Modifier.fillMaxWidth().height(56.dp),
        ) {
            Text(stringResource(Res.string.playAgainButton), fontSize = 20.sp)
        }
    }
}

/**
 * Composable representing a single row in the scoreboard.
 *
 * @param label The label to display (player name or "Ties").
 * @param score The numeric score value.
 */
@Composable
fun ScoreRow(label: String, score: Int) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(label, style = MaterialTheme.typography.bodyLarge)
        Text(score.toString(), style = MaterialTheme.typography.headlineSmall)
    }
}
