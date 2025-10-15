package com.ikiugu.pokedex.domain.usecase

import com.ikiugu.pokedex.domain.entity.PokemonDetail
import com.ikiugu.pokedex.domain.error.Result
import com.ikiugu.pokedex.domain.repository.PokemonRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPokemonDetailUseCase @Inject constructor(
    private val repository: PokemonRepository
) {
    operator fun invoke(id: Int): Flow<Result<PokemonDetail>> {
        return repository.getPokemonDetail(id)
    }
}
