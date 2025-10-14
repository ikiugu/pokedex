package com.ikiugu.oldmutual.domain.usecase

import com.ikiugu.oldmutual.domain.entity.Pokemon
import com.ikiugu.oldmutual.domain.error.Result
import com.ikiugu.oldmutual.domain.repository.PokemonRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPokemonListUseCase @Inject constructor(
    private val repository: PokemonRepository
) {
    operator fun invoke(page: Int = 0, pageSize: Int = 20): Flow<Result<List<Pokemon>>> {
        return repository.getPokemonList(page, pageSize)
    }
}
