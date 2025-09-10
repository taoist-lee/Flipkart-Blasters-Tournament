package com.leedroid.blasterstournament.ui.matches

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.leedroid.blasterstournament.domain.model.Result
import com.leedroid.blasterstournament.ui.components.AppDivider
import com.leedroid.blasterstournament.ui.components.SimpleTopBar

@Composable
fun PlayerMatchesRoute(
    playerId: Int, viewModel: PlayerMatchesViewModel, onBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    LaunchedEffect(playerId) { viewModel.load(playerId) }
    PlayerMatchesScreen(state = state, onBack = onBack)
}

@Composable
fun PlayerMatchesScreen(state: PlayerMatchesUiState, onBack: () -> Unit) {
    when (state) {
        PlayerMatchesUiState.Loading -> Box(
            Modifier.fillMaxSize(), contentAlignment = Alignment.Center
        ) { CircularProgressIndicator() }

        is PlayerMatchesUiState.Error -> Box(
            Modifier.fillMaxSize(), contentAlignment = Alignment.Center
        ) { Text(state.message) }

        is PlayerMatchesUiState.Data -> Scaffold(
            topBar = {
                SimpleTopBar(
                    title = state.player.name,
                    showBack = true,
                    onBack = onBack
                )
            }) { inner ->
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(inner)
            ) {
                Text(
                    "Matches",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(16.dp)
                )
                AppDivider()
                LazyColumn(Modifier.fillMaxSize()) {
                    items(state.matches) { row ->
                        val bg = when (row.resultForPlayer) {
                            Result.WIN -> Color(0xFF2E7D32)
                            Result.LOSS -> Color(0xFFC62828)
                            Result.DRAW -> Color.White
                        }
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .background(bg)
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                row.leftName,
                                modifier = Modifier.weight(1f),
                                color = if (row.resultForPlayer == Result.DRAW) MaterialTheme.colorScheme.onSurface else Color.White
                            )
                            Text(
                                "${row.leftScore} - ${row.rightScore}",
                                fontWeight = FontWeight.Medium,
                                color = if (row.resultForPlayer == Result.DRAW) MaterialTheme.colorScheme.onSurface else Color.White
                            )
                            Text(
                                row.rightName,
                                modifier = Modifier.weight(1f),
                                textAlign = androidx.compose.ui.text.style.TextAlign.End,
                                color = if (row.resultForPlayer == Result.DRAW) MaterialTheme.colorScheme.onSurface else Color.White
                            )
                        }
                        AppDivider()
                    }
                }
            }
        }
    }
}
