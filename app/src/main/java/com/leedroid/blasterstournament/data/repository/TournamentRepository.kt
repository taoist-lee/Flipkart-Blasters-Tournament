package com.leedroid.blasterstournament.data.repository

import com.leedroid.blasterstournament.data.api.BlasterApi
import com.leedroid.blasterstournament.data.dto.MatchDto
import com.leedroid.blasterstournament.data.dto.PlayerDto
import com.leedroid.blasterstournament.domain.model.Match
import com.leedroid.blasterstournament.domain.model.Player
import com.leedroid.blasterstournament.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TournamentRepository @Inject constructor(
    private val api: BlasterApi,
    @IoDispatcher private val io: CoroutineDispatcher
) {
    private val _players = MutableStateFlow<List<Player>>(emptyList())
    val players: StateFlow<List<Player>> = _players

    private val _matches = MutableStateFlow<List<Match>>(emptyList())
    val matches: StateFlow<List<Match>> = _matches

    private var loaded = false

    suspend fun refresh(force: Boolean = false) = withContext(io) {
        if (loaded && !force) return@withContext
        val matchesDto = api.getMatches()
        val playersDto = api.getPlayers()
        _players.value = playersDto.map { it.toDomain() }
        _matches.value = matchesDto.map { it.toDomain() }
        loaded = true
    }

    private fun PlayerDto.toDomain() = Player(id, name, icon = icon.replace("http://", "https://"))
    private fun MatchDto.toDomain() = Match(
        matchNumber = match,
        player1Id = player1.id,
        player1Score = player1.score,
        player2Id = player2.id,
        player2Score = player2.score
    )
}
