package com.leedroid.blasterstournament.domain

import com.leedroid.blasterstournament.domain.model.Match
import com.leedroid.blasterstournament.domain.model.Player
import com.leedroid.blasterstournament.domain.model.PlayerMatchRow
import com.leedroid.blasterstournament.domain.model.PointsSortOrder
import com.leedroid.blasterstournament.domain.model.Result
import com.leedroid.blasterstournament.domain.model.Standing

object StandingsCalculator {
    data class Aggregate(
        var points: Int = 0,
        var totalScore: Int = 0,
        var matches: Int = 0,
        var wins: Int = 0,
        var draws: Int = 0,
        var losses: Int = 0
    )

    fun computeStandings(players: List<Player>, matches: List<Match>): List<Standing> {
        val agg = players.associate { it.id to Aggregate() }.toMutableMap()
        matches.forEach { m ->
            val p1 = agg[m.player1Id]
            val p2 = agg[m.player2Id]
            if (p1 == null || p2 == null) return@forEach
            p1.matches++
            p2.matches++
            p1.totalScore += m.player1Score
            p2.totalScore += m.player2Score
            when {
                m.player1Score > m.player2Score -> {
                    p1.points += 3; p1.wins++; p2.losses++
                }

                m.player2Score > m.player1Score -> {
                    p2.points += 3; p2.wins++; p1.losses++
                }

                else -> {
                    p1.points += 1; p2.points += 1; p1.draws++; p2.draws++
                }
            }
        }
        return players.map { pl ->
            val a = agg[pl.id] ?: Aggregate()
            Standing(
                playerId = pl.id,
                name = pl.name,
                icon = pl.icon,
                points = a.points,
                totalScore = a.totalScore,
                matchesPlayed = a.matches,
                wins = a.wins,
                draws = a.draws,
                losses = a.losses
            )
        }
    }

    fun sortStandings(list: List<Standing>, order: PointsSortOrder): List<Standing> =
        list.sortedWith(compareBy<Standing> {
            when (order) {
                PointsSortOrder.ASC -> it.points
                PointsSortOrder.DESC -> -it.points
            }
        }.thenByDescending { it.totalScore }.thenBy { it.name })

    fun buildPlayerMatchRows(
        playerId: Int, players: Map<Int, Player>, matches: List<Match>
    ): List<PlayerMatchRow> {
        return matches.filter { it.player1Id == playerId || it.player2Id == playerId }
            .sortedByDescending { it.matchNumber }.map { m ->
                val isRight = m.player2Id == playerId
                val leftPlayer = players[m.player1Id]
                val rightPlayer = players[m.player2Id]
                val leftName = leftPlayer?.name ?: "Unknown"
                val rightName = rightPlayer?.name ?: "Unknown"
                val result = when {
                    m.player1Score == m.player2Score -> Result.DRAW
                    m.player1Score > m.player2Score && playerId == m.player1Id -> Result.WIN
                    m.player2Score > m.player1Score && playerId == m.player2Id -> Result.WIN
                    else -> Result.LOSS
                }
                PlayerMatchRow(
                    matchNumber = m.matchNumber,
                    leftName = leftName,
                    rightName = rightName,
                    leftScore = m.player1Score,
                    rightScore = m.player2Score,
                    resultForPlayer = result,
                    isPlayerOnRight = isRight
                )
            }
    }
}

