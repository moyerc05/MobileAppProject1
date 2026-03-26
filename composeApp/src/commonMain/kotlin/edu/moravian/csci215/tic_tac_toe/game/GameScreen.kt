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
import tictactoe.composeapp.generated.resources.Res
import tictactoe.composeapp.generated.resources.*

/**
 * Main gameplay screen. This handles:
 *
 * Board rendering
 * Player turns
 * AI delays
 * Error handling via snackbar
 * Triggering game over navigation
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
            modifier = Modifier.fillMaxWidth(0.9f), // Scaled to 90% of screen width
            onCellClick = { r, c ->

                if (isAiThinking) {
                    coroutineScope.launch {
                        showSnackbar("Wait for AI move!")
                    }
                    return@GameBoard
                }

                val newBoard = board.playPiece(r, c)

                if (newBoard == null) {
                    coroutineScope.launch {
                        showSnackbar("Spot already taken!")
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
 * Displays the 3x3 tic-tac-toe board.
 */
@Composable
fun GameBoard(
    board: Board,
    onCellClick: (Int, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.aspectRatio(1f), // Forces the board to always be a perfect square
        contentAlignment = Alignment.Center
    ) {

        // Background board image
        Image(
            painter = painterResource(resource = Res.drawable.board),
            contentDescription = "Tic Tac Toe Board",
            modifier = Modifier.matchParentSize(),
            contentScale = ContentScale.FillBounds
        )

        // Overlay clickable grid
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(15.dp)
        ){
            for (r in 0..2) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f) // Distributes row heights evenly
                ){
                    for (c in 0..2) {
                        BoardCell(
                            value = board[r, c],
                            onClick = { onCellClick(r, c) },
                            modifier = Modifier
                                .fillMaxHeight()
                                .weight(1f) // Distributes column widths evenly
                        )
                    }
                }
            }
        }
    }
}

/**
 * A single cell in the tic-tac-toe grid.
 */
@Composable
fun BoardCell(
    value: Char,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .padding(4.dp)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        when (value) {
            'X' -> Image(
                painter = painterResource(Res.drawable.x),
                contentDescription = "X",
                modifier = Modifier.fillMaxSize(0.7f) // Scales the X to take up 70% of the cell
            )

            'O' -> Image(
                painter = painterResource(Res.drawable.o),
                contentDescription = "O",
                modifier = Modifier.fillMaxSize(0.7f) // Scales the O to take up 70% of the cell
            )
        }
    }
}

/**
 * Displays whose turn it is.
 */
@Composable
fun CurrentPlayerText(
    name: String,
    piece: Char
) {
    Text(
        text = "$name's turn ($piece)",
        style = MaterialTheme.typography.headlineMedium
    )
}

/*
Things to do:
- String resources for all text
- Icons for back button
- Background of OS status bars (top and bottom) should be app's primary color
- Connection to GameOverScreen after navigation is set up
- Fix scaffold situation (only one scaffold in app)
 */
