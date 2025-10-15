package com.ikiugu.pokedex.presentation.ui.state

import com.ikiugu.pokedex.domain.entity.PokemonDetail
import com.ikiugu.pokedex.domain.error.PokemonError

data class PokemonDetailState(
    val pokemonDetail: PokemonDetail? = null,
    val isLoading: Boolean = false,
    val error: PokemonError? = null
)
