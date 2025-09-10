package com.leedroid.blasterstournament.ui.matches

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.leedroid.blasterstournament.data.repository.TournamentRepository
import com.leedroid.blasterstournament.domain.StandingsCalculator
import com.leedroid.blasterstournament.domain.model.Player
import com.leedroid.blasterstournament.domain.model.PlayerMatchRow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface PlayerMatchesUiState {
    object Loading: PlayerMatchesUiState
    data class Error(val message: String): PlayerMatchesUiState
    data class Data(val player: Player, val matches: List<PlayerMatchRow>): PlayerMatchesUiState
}

@HiltViewModel
class PlayerMatchesViewModel @Inject constructor(
    private val repo: TournamentRepository
): ViewModel() {

    private val _state = MutableStateFlow<PlayerMatchesUiState>(PlayerMatchesUiState.Loading)
    val state: StateFlow<PlayerMatchesUiState> = _state

    fun load(playerId: Int) {
        viewModelScope.launch {
            _state.value = PlayerMatchesUiState.Loading
            try {
                repo.refresh() // no-op if already loaded
                combine(repo.players, repo.matches) { players, matches ->
                    val player = players.firstOrNull { it.id == playerId }
                        ?: return@combine PlayerMatchesUiState.Error("Player not found")
                    val rows = StandingsCalculator.buildPlayerMatchRows(playerId, player, players.associateBy { it.id }, matches)
                    PlayerMatchesUiState.Data(player, rows)
                }.collect { _state.value = it }
            } catch (e: Exception) {
                _state.value = PlayerMatchesUiState.Error(e.message ?: "Error")
            }
        }
    }
}

