package com.ikiugu.pokedex.presentation.navigation

object Routes {
    const val HOME = "home_screen"
    const val DETAIL = "pokemon_detail_screen"
    const val ARG_POKEMON_ID = "pokemonId"
}

sealed class PokemonDestinations(val route: String) {
    data object Home : PokemonDestinations(Routes.HOME)
    data object Detail : PokemonDestinations("${Routes.DETAIL}/{${Routes.ARG_POKEMON_ID}}") {
        const val ARG_POKEMON_ID = Routes.ARG_POKEMON_ID
        fun createRoute(pokemonId: Int) = "${Routes.DETAIL}/$pokemonId"
    }
}


