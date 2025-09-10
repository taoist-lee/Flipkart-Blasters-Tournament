package com.leedroid.blasterstournament.ui.points

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.leedroid.blasterstournament.data.repository.TournamentRepository
import com.leedroid.blasterstournament.domain.StandingsCalculator
import com.leedroid.blasterstournament.domain.model.PointsSortOrder
import com.leedroid.blasterstournament.domain.model.Standing
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface PointsTableUiState {
    object Loading: PointsTableUiState
    data class Error(val message: String): PointsTableUiState
    data class Data(
        val standings: List<Standing>,
        val sortOrder: PointsSortOrder
    ): PointsTableUiState
}

@HiltViewModel
class PointsTableViewModel @Inject constructor(
    private val repo: TournamentRepository
): ViewModel() {

    private val sortOrder = MutableStateFlow(PointsSortOrder.DESC)
    private val _state: MutableStateFlow<PointsTableUiState> = MutableStateFlow(PointsTableUiState.Loading)
    val state: StateFlow<PointsTableUiState> = _state

    init {
        viewModelScope.launch {
            _state.value = PointsTableUiState.Loading
            try {
                repo.refresh()
            } catch (e: Exception) {
                _state.value = PointsTableUiState.Error(e.message ?: "Error")
            }
        }
        viewModelScope.launch {
            combine(repo.players, repo.matches, sortOrder) { players, matches, order ->
                val standings = StandingsCalculator.computeStandings(players, matches)
                val sorted = StandingsCalculator.sortStandings(standings, order)
                PointsTableUiState.Data(sorted, order)
            }.collect { _state.value = it }
        }
    }

    fun toggleSort() { sortOrder.update { if (it == PointsSortOrder.DESC) PointsSortOrder.ASC else PointsSortOrder.DESC } }
    fun retry() { viewModelScope.launch { try { repo.refresh(force = true) } catch (e: Exception) { _state.value = PointsTableUiState.Error(e.message ?: "Error") } } }
}

