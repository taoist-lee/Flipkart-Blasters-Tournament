package com.leedroid.blasterstournament.data.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MatchDto(
    val match: Int, val player1: PlayerScoreDto, val player2: PlayerScoreDto
) {
    data class PlayerScoreDto(
        val id: Int, val score: Int
    )
}