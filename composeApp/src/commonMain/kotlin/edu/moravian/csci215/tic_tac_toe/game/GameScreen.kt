package edu.moravian.csci215.tic_tac_toe

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// This code is almost entirely AI and acts as a placeholder to show that we're even
// moving in navigation.

@Composable
fun GameScreen(
    p1Name: String,
    p2Name: String,
    p1Type: PlayerType,
    p2Type: PlayerType,
    onNavigateBack: () -> Unit, // Callback to trigger the back navigation
) {
    // This Box centers everything on the screen
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(16.dp),
        ) {
            // --- BACK BUTTON ---
            // We use align(Alignment.Start) to push it to the left side of the Column
            OutlinedButton(
                onClick = onNavigateBack,
                modifier = Modifier.align(Alignment.Start),
            ) {
                Text("< Back to Setup")
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 1. The Matchup Header
            Text(
                text = "Matchup",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "$p1Name (${p1Type.name.replace("_", " ")})",
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.primary,
            )

            Text(
                text = "VS",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 4.dp),
            )

            Text(
                text = "$p2Name (${p2Type.name.replace("_", " ")})",
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.secondary,
            )

            Spacer(modifier = Modifier.height(48.dp))

            // 2. Placeholder for the Board
            Text(
                text = "[ Tic-Tac-Toe Board Goes Here ]",
                fontSize = 24.sp,
                fontWeight = FontWeight.Light,
            )

            Spacer(modifier = Modifier.height(48.dp))

            // 3. Placeholder for Game Status/Controls
            Text(
                text = "Waiting for game to start...",
                fontSize = 18.sp,
            )
        }
    }
}
