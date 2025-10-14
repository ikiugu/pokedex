package com.ikiugu.oldmutual.domain.usecase

import androidx.paging.PagingData
import com.ikiugu.oldmutual.domain.entity.Pokemon
import com.ikiugu.oldmutual.domain.repository.PokemonRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPokemonListUseCase @Inject constructor(
    private val repository: PokemonRepository
) {
    operator fun invoke(): Flow<PagingData<Pokemon>> {
        return repository.getPokemonList()
    }
}
