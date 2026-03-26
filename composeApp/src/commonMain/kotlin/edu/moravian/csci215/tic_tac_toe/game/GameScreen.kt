package edu.moravian.csci215.tic_tac_toe.game

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import edu.moravian.csci215.tic_tac_toe.PlayerType
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import tictactoe.composeapp.generated.resources.Res
import tictactoe.composeapp.generated.resources.*

/**
 * Main gameplay screen.
 *
 * Displays the current player's turn, the Tic-Tac-Toe board, and handles user input
 * as well as AI moves. Handles game over detection.
 *
 * @param p1Name Name of player 1.
 * @param p2Name Name of player 2.
 * @param p1Type Type of player 1 (HUMAN or AI difficulty).
 * @param p2Type Type of player 2 (HUMAN or AI difficulty).
 * @param showSnackbar Callback to display temporary messages to the user.
 * @param onGameOver Callback invoked when the game ends. Provides the winner ('X' or 'O') or null if tie, and final board state.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(
    p1Name: String,
    p2Name: String,
    p1Type: PlayerType,
    p2Type: PlayerType,
    showSnackbar: (String) -> Unit,
    onGameOver: (Char?, Board) -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()

    var board by remember { mutableStateOf(Board()) }
    var isAiThinking by remember { mutableStateOf(false) }
    val waitForAIMsg = stringResource(Res.string.waitForAIMove)
    val spotTakenMsg = stringResource(Res.string.boardSpotTaken)

    val currentPlayerName =
        if (board.turn == 'X') p1Name else p2Name

    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeContentPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {

        CurrentPlayerText(currentPlayerName, board.turn)

        GameBoard(
            board = board,
            modifier = Modifier
                .weight(1f),
            onCellClick = { r, c ->

                if (isAiThinking) {
                    coroutineScope.launch {
                        showSnackbar(waitForAIMsg)
                    }
                    return@GameBoard
                }

                val newBoard = board.playPiece(r, c)

                if (newBoard == null) {
                    coroutineScope.launch {
                        showSnackbar(spotTakenMsg)
                    }
                } else {
                    board = newBoard
                }
            }
        )
    }

    // GAME OVER LOGIC
    LaunchedEffect(board.isGameOver) {
        if (board.isGameOver) {
            // Brief pause so the user can see the final piece placed on the board
            delay(500)

            val winner = when {
                board.hasWon('X') -> 'X'
                board.hasWon('O') -> 'O'
                else -> null // Tie
            }
            onGameOver(winner, board)
        }
    }

    // AI LOGIC
    LaunchedEffect(board) {
        val currentType =
            if (board.turn == 'X') p1Type else p2Type

        if (currentType != PlayerType.HUMAN && !board.isGameOver) {

            isAiThinking = true
            delay(800)

            val ai: AIPlayer = when (currentType) {
                PlayerType.EASY_AI -> EasyAIPlayer()
                PlayerType.MEDIUM_AI -> MediumAIPlayer()
                PlayerType.HARD_AI -> HardAIPlayer()
                else -> return@LaunchedEffect
            }

            val (r, c) = ai.findMove(board, board.turn)

            board = board.playPiece(r, c) ?: board

            isAiThinking = false
        }
    }
}

/**
 * Composable that displays the Tic-Tac-Toe board.
 *
 * @param board The current board state.
 * @param onCellClick Callback invoked when a specific cell is clicked, providing row and column.
 * @param modifier Optional Compose modifier.
 */
@Composable
fun GameBoard(
    board: Board,
    onCellClick: (Int, Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.aspectRatio(1f), // Forces the board to always be a perfect square
        contentAlignment = Alignment.Center,
    ) {
        // Background board image
        Image(
            painter = painterResource(resource = Res.drawable.board),
            contentDescription = stringResource(Res.string.gameBoardTitleDesc),
            modifier = Modifier.matchParentSize(),
            contentScale = ContentScale.FillBounds,
        )

        // Overlay clickable grid
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(15.dp),
        ){
            for (r in 0..2) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f), // Distributes row heights evenly
                ){
                    for (c in 0..2) {
                        BoardCell(
                            value = board[r, c],
                            onClick = { onCellClick(r, c) },
                            modifier = Modifier
                                .fillMaxHeight()
                                .weight(1f), // Distributes column widths evenly
                        )
                    }
                }
            }
        }
    }
}

/**
 * A single cell in the Tic-Tac-Toe grid.
 *
 * @param value The current value of the cell ('X', 'O', or empty).
 * @param onClick Callback invoked when the cell is clicked.
 * @param modifier Optional Compose modifier.
 */
@Composable
fun BoardCell(
    value: Char,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .padding(4.dp)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        when (value) {
            'X' -> Image(
                painter = painterResource(Res.drawable.x),
                contentDescription = stringResource(Res.string.PlayerX),
                modifier = Modifier.fillMaxSize(0.7f), // Scales the X to take up 70% of the cell
            )

            'O' -> Image(
                painter = painterResource(Res.drawable.o),
                contentDescription = stringResource(Res.string.PlayerO),
                modifier = Modifier.fillMaxSize(0.7f), // Scales the O to take up 70% of the cell
            )
        }
    }
}

/**
 * Displays the name of the current player and their piece.
 *
 * @param name Name of the current player.
 * @param piece Character representing the player's piece ('X' or 'O').
 */
@Composable
fun CurrentPlayerText(
    name: String,
    piece: Char,
) {
    Text(
        text = "$name's turn ($piece)",
        style = MaterialTheme.typography.headlineMedium,
    )
}
