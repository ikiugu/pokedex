package com.ikiugu.pokedex.domain.repository

import androidx.paging.PagingData
import com.ikiugu.pokedex.domain.entity.Pokemon
import com.ikiugu.pokedex.domain.entity.PokemonDetail
import com.ikiugu.pokedex.domain.error.Result
import kotlinx.coroutines.flow.Flow

interface PokemonRepository {
    fun getPokemonList(): Flow<PagingData<Pokemon>>
    fun getPokemonDetail(id: Int): Flow<Result<PokemonDetail>>
    fun searchPokemon(query: String): Flow<Result<List<Pokemon>>>
}
