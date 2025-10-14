package com.ikiugu.oldmutual.domain.repository

import androidx.paging.PagingData
import com.ikiugu.oldmutual.domain.entity.Pokemon
import com.ikiugu.oldmutual.domain.entity.PokemonDetail
import com.ikiugu.oldmutual.domain.error.Result
import kotlinx.coroutines.flow.Flow

interface PokemonRepository {
    fun getPokemonList(): Flow<PagingData<Pokemon>>
    fun getPokemonDetail(id: Int): Flow<Result<PokemonDetail>>
    fun searchPokemon(query: String): Flow<Result<List<Pokemon>>>
}
