package com.leedroid.blasterstournament.data.api

import com.leedroid.blasterstournament.data.dto.MatchDto
import com.leedroid.blasterstournament.data.dto.PlayerDto
import retrofit2.http.GET

interface BlasterApi {
    @GET("b/JNYL")
    suspend fun getMatches(): List<MatchDto>

    @GET("b/IKQQ")
    suspend fun getPlayers(): List<PlayerDto>
}