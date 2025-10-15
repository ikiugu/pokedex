package com.ikiugu.pokedex.presentation.ui.state

import com.ikiugu.pokedex.domain.entity.Pokemon
import com.ikiugu.pokedex.domain.error.PokemonError

data class PokemonListState(
    val pokemonList: List<Pokemon> = emptyList(),
    val searchResults: List<Pokemon> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val error: PokemonError? = null,
    val searchQuery: String = "",
    val hasReachedEnd: Boolean = false
)
