package edu.moravian.csci215.tic_tac_toe

import androidx.compose.runtime.Composable
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

/**
 * Represents the possible player types that can be selected
 * on the welcome screen.
 */
enum class PlayerType {
    HUMAN,
    EASY_AI,
    MEDIUM_AI,
    HARD_AI
}
@Composable
fun WelcomeScreen() {

    // UI state (temporary for layout preview)
    var player1Name by remember { mutableStateOf("Player 1") }
    var player2Name by remember { mutableStateOf("Player 2") }

    var player1Type by remember { mutableStateOf(PlayerType.HUMAN) }
    var player2Type by remember { mutableStateOf(PlayerType.HUMAN) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(32.dp))

        TitleSection()

        Spacer(modifier = Modifier.height(200.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            PlayerSetupColumn(
                modifier = Modifier.weight(1f),
                playerLabel = "Player 1",
                playerName = player1Name,
                onNameChange = { player1Name = it },
                playerType = player1Type,
                onTypeChange = { player1Type = it }
            )

            PlayerSetupColumn(
                modifier = Modifier.weight(1f),
                playerLabel = "Player 2",
                playerName = player2Name,
                onNameChange = { player2Name = it },
                playerType = player2Type,
                onTypeChange = { player2Type = it }
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        StartGameButton()
    }
}

@Composable
fun TitleSection() {
    Text(
        text = "Welcome to Tic-Tac-Toe!",
        style = MaterialTheme.typography.headlineLarge.copy(
            fontSize = 50.sp,
            lineHeight = 70.sp
        ),
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun PlayerSetupColumn(
    modifier: Modifier = Modifier,
    playerLabel: String,
    playerName: String,
    onNameChange: (String) -> Unit,
    playerType: PlayerType,
    onTypeChange: (PlayerType) -> Unit
) {

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = playerLabel,
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(8.dp))

        PlayerTypeDropdown(
            selectedType = playerType,
            onTypeSelected = onTypeChange
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = playerName,
            onValueChange = onNameChange,
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun PlayerTypeDropdown(
    selectedType: PlayerType,
    onTypeSelected: (PlayerType) -> Unit
) {

    var expanded by remember { mutableStateOf(false) }

    Box {

        OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = selectedType.name.replace("_", " "))
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {

            PlayerType.entries.forEach { type ->

                DropdownMenuItem(
                    text = { Text(type.name.replace("_", " ")) },
                    onClick = {
                        onTypeSelected(type)
                        expanded = false
                    }
                )

            }
        }
    }
}

@Composable
fun StartGameButton() {

    Button(
        onClick = { /* add functionality later */ },
        modifier = Modifier
            .width(120.dp)
            .height(50.dp)
    ) {
        Text("Start!",
            style = MaterialTheme.typography.titleLarge.copy(
            fontSize = 25.sp,
        ))
    }
}