package edu.moravian.csci215.tic_tac_toe.game

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import tictactoe.composeapp.generated.resources.Res
import tictactoe.composeapp.generated.resources.*

@Composable
actual fun PlatformBackButtonIcon() {
    Image(
        // Make sure your downloaded PNG is named back_ios.png
        painter = painterResource(Res.drawable.arrow_back_ios),
        contentDescription = "Back",
        modifier = Modifier.size(24.dp)
    )
}