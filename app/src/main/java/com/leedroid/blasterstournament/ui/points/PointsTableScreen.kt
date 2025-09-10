package com.leedroid.blasterstournament.ui.points

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.leedroid.blasterstournament.domain.model.PointsSortOrder
import com.leedroid.blasterstournament.domain.model.Standing
import com.leedroid.blasterstournament.ui.components.AppDivider
import com.leedroid.blasterstournament.ui.components.SimpleTopBar

@Composable
fun PointsTableRoute(
    viewModel: PointsTableViewModel, onPlayerClick: (Int) -> Unit
) {
    val state by viewModel.state.collectAsState()
    PointsTableScreen(
        state = state,
        onToggleSort = viewModel::toggleSort,
        onPlayerClick = onPlayerClick,
        onRetry = viewModel::retry
    )
}

@Composable
fun PointsTableScreen(
    state: PointsTableUiState,
    onToggleSort: () -> Unit,
    onPlayerClick: (Int) -> Unit,
    onRetry: () -> Unit
) {
    Scaffold(
        topBar = { SimpleTopBar(title = "Star Wars Blaster Tournament") }) { inner ->
        when (state) {
            PointsTableUiState.Loading -> Box(
                Modifier
                    .fillMaxSize()
                    .padding(inner), contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }

            is PointsTableUiState.Error -> Box(
                Modifier
                    .fillMaxSize()
                    .padding(inner), contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(state.message)
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = onRetry) { Text("Retry") }
                }
            }

            is PointsTableUiState.Data -> StandingsList(
                modifier = Modifier.padding(inner),
                standings = state.standings,
                order = state.sortOrder,
                onToggleSort = onToggleSort,
                onPlayerClick = onPlayerClick
            )
        }
    }
}

@Composable
private fun StandingsList(
    modifier: Modifier = Modifier,
    standings: List<Standing>,
    order: PointsSortOrder,
    onToggleSort: () -> Unit,
    onPlayerClick: (Int) -> Unit
) {
    Column(modifier.fillMaxSize()) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Points Table", style = MaterialTheme.typography.titleMedium)
            TextButton(onClick = onToggleSort) { Text(if (order == PointsSortOrder.DESC) "Sort Asc" else "Sort Desc") }
        }
        AppDivider()
        if (standings.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("No data") }
        } else {
            LazyColumn(Modifier.fillMaxSize()) {
                items(standings) { standing ->
                    StandingRow(standing = standing) { onPlayerClick(standing.playerId) }
                    AppDivider()
                }
            }
        }
    }
}

@Composable
private fun StandingRow(standing: Standing, onClick: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().clickable { onClick() }.padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = standing.icon,
            contentDescription = standing.name,
            modifier = Modifier.size(56.dp),
            contentScale = ContentScale.Crop
        )
        Spacer(Modifier.width(16.dp))
        Text(standing.name, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
        Text(standing.points.toString(), style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
    }
}