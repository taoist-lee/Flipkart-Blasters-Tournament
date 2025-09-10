package com.leedroid.blasterstournament.data.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PlayerDto(
    val id: Int, val name: String, val icon: String
)