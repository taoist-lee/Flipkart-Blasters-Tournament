package com.leedroid.blasterstournament.domain.model

/** Basic player model */
data class Player(
    val id: Int,
    val name: String,
    val icon: String
)

/** Match between two players. matchNumber used as chronological ordering (higher = newer) */
data class Match(
    val matchNumber: Int,
    val player1Id: Int,
    val player1Score: Int,
    val player2Id: Int,
    val player2Score: Int
)

enum class Result { WIN, LOSS, DRAW }

data class Standing(
    val playerId: Int,
    val name: String,
    val icon: String,
    val points: Int,
    val totalScore: Int,
    val matchesPlayed: Int,
    val wins: Int,
    val draws: Int,
    val losses: Int
)

data class PlayerMatchRow(
    val matchNumber: Int,
    val leftName: String,
    val rightName: String,
    val leftScore: Int,
    val rightScore: Int,
    val resultForPlayer: Result,
    val isPlayerOnRight: Boolean
)

enum class PointsSortOrder { DESC, ASC }

