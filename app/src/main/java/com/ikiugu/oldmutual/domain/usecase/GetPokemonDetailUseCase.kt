package com.ikiugu.oldmutual.domain.usecase

import com.ikiugu.oldmutual.domain.entity.PokemonDetail
import com.ikiugu.oldmutual.domain.error.Result
import com.ikiugu.oldmutual.domain.repository.PokemonRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPokemonDetailUseCase @Inject constructor(
    private val repository: PokemonRepository
) {
    operator fun invoke(id: Int): Flow<Result<PokemonDetail>> {
        return repository.getPokemonDetail(id)
    }
}
