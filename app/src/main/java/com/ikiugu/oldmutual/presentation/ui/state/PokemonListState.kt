package com.ikiugu.oldmutual.presentation.ui.state

import com.ikiugu.oldmutual.domain.entity.Pokemon
import com.ikiugu.oldmutual.domain.error.PokemonError

data class PokemonListState(
    val pokemonList: List<Pokemon> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val error: PokemonError? = null,
    val searchQuery: String = "",
    val hasReachedEnd: Boolean = false
)
