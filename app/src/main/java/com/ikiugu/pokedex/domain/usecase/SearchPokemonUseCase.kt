package com.ikiugu.pokedex.domain.usecase

import com.ikiugu.pokedex.domain.entity.Pokemon
import com.ikiugu.pokedex.domain.error.Result
import com.ikiugu.pokedex.domain.repository.PokemonRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchPokemonUseCase @Inject constructor(
    private val repository: PokemonRepository
) {
    operator fun invoke(query: String): Flow<Result<List<Pokemon>>> {
        return repository.searchPokemon(query)
    }
}
