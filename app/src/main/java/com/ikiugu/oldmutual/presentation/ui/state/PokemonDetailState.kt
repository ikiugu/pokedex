package com.ikiugu.oldmutual.presentation.ui.state

import com.ikiugu.oldmutual.domain.entity.PokemonDetail
import com.ikiugu.oldmutual.domain.error.PokemonError

data class PokemonDetailState(
    val pokemonDetail: PokemonDetail? = null,
    val isLoading: Boolean = false,
    val error: PokemonError? = null
)
