package com.ikiugu.oldmutual.data.remote.api

import com.ikiugu.oldmutual.data.remote.dto.PokemonListResponse
import com.ikiugu.oldmutual.data.remote.dto.PokemonResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PokemonApiService {
    @GET("pokemon")
    suspend fun getPokemonList(
        @Query("limit") limit: Int,
        @Query("offset") offset: Int
    ): Response<PokemonListResponse>

    @GET("pokemon/{id}")
    suspend fun getPokemonDetail(@Path("id") id: Int): Response<PokemonResponse>
}
