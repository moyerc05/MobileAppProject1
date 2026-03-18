package edu.moravian.csci215.tic_tac_toe

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import tictactoe.composeapp.generated.resources.Res
import tictactoe.composeapp.generated.resources.*
/**
 * Represents the possible player types that can be selected
 * on the welcome screen.
 */
enum class PlayerType {
    HUMAN,
    EASY_AI,
    MEDIUM_AI,
    HARD_AI,
}

// A list of default names to pull from when the screen loads
val defaultNames = listOf("Alice", "Bob", "Charlie", "Diana", "Eve", "Frank", "Grace", "Hank", "Ivy", "Jack", "Karen",
    "Leo",
)

@Composable
fun WelcomeScreen(
    onStartGame: (String, String, PlayerType, PlayerType) -> Unit,
) {
    // 1. Grab two unique, random names ONCE when the screen first loads
    val initialNames = remember { defaultNames.shuffled().take(2) }

    // UI state
    var player1Name by remember { mutableStateOf(initialNames[0]) }
    var player2Name by remember { mutableStateOf(initialNames[1]) }

    var player1Type by remember { mutableStateOf(PlayerType.HUMAN) }
    var player2Type by remember { mutableStateOf(PlayerType.HUMAN) }

    // Snackbar and Coroutine state for validation messages
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { paddingValues ->

        // All of this spacing needs to be heavily refactored
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            val isLandscape = maxWidth > maxHeight

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                // Shrink top spacing in landscape
                //Spacer(modifier = Modifier.height(if (isLandscape) 8.dp else 32.dp))
                if (!isLandscape) {
                    Spacer(modifier = Modifier.height(48.dp)) // Increase this to push it down further
                }
                TitleSection(isLandscape = isLandscape)

                // Drastically reduce this spacer in landscape mode
                Spacer(modifier = Modifier.height(if (isLandscape) 24.dp else 200.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    PlayerSetupColumn(
                        modifier = Modifier.weight(1f),
                        playerLabel = "Player 1",
                        playerName = player1Name,
                        onNameChange = { player1Name = it },
                        playerType = player1Type,
                        onTypeChange = { player1Type = it },
                    )

                    PlayerSetupColumn(
                        modifier = Modifier.weight(1f),
                        playerLabel = "Player 2",
                        playerName = player2Name,
                        onNameChange = { player2Name = it },
                        playerType = player2Type,
                        onTypeChange = { player2Type = it },
                    )
                }

                // Shrink bottom spacing in landscape
                // Might have to rework this when we add a logo
                Spacer(modifier = Modifier.height(if (isLandscape) 16.dp else 32.dp))

                StartGameButton(
                    onStartClick = {
                        // Validation Logic
                        if (player1Name.isBlank() || player2Name.isBlank()) {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Player names cannot be empty!")
                            }
                        } else {
                            // Trigger navigation callback
                            onStartGame(player1Name, player2Name, player1Type, player2Type)
                        }
                    },
                )
            }
        }
    }
}

@Composable
fun TitleSection(isLandscape: Boolean) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        // Portrays landscape
        if (!isLandscape) {
            Image(
                painter = painterResource(resource = Res.drawable.tictactoe),
                contentDescription = "Tic-Tac-Toe App Logo",
                modifier = Modifier.size(120.dp) // Adjust this size as needed
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
        Text(
            text = "Welcome to Tic-Tac-Toe!",
            style = MaterialTheme.typography.headlineLarge.copy(
                fontSize = 50.sp,
                lineHeight = 70.sp,
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
@Composable
fun PlayerSetupColumn(
    modifier: Modifier = Modifier,
    playerLabel: String,
    playerName: String,
    onNameChange: (String) -> Unit,
    playerType: PlayerType,
    onTypeChange: (PlayerType) -> Unit,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = playerLabel,
            style = MaterialTheme.typography.titleLarge,
        )

        Spacer(modifier = Modifier.height(8.dp))

        PlayerTypeDropdown(
            selectedType = playerType,
            onTypeSelected = onTypeChange,
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = playerName,
            onValueChange = onNameChange,
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
fun PlayerTypeDropdown(
    selectedType: PlayerType,
    onTypeSelected: (PlayerType) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(text = selectedType.name.replace("_", " "))
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            PlayerType.entries.forEach { type ->
                DropdownMenuItem(
                    text = { Text(type.name.replace("_", " ")) },
                    onClick = {
                        onTypeSelected(type)
                        expanded = false
                    },
                )
            }
        }
    }
}

@Composable
fun StartGameButton(onStartClick: () -> Unit) {
    Button(
        onClick = onStartClick,
        modifier = Modifier
            .width(120.dp)
            .height(50.dp),
    ) {
        Text(
            "Start!",
            style = MaterialTheme.typography.titleLarge.copy(
                fontSize = 25.sp,
            ),
        )
    }
}
