package com.ikiugu.pokedex.presentation.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ikiugu.pokedex.presentation.ui.screen.HomeScreen
import com.ikiugu.pokedex.presentation.ui.screen.PokemonDetailScreen

@Composable
fun PokemonNavigation(
    paddingValues: PaddingValues,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = PokemonDestinations.Home.route
    ) {
        composable(PokemonDestinations.Home.route) {
            HomeScreen(
                onPokemonClick = { pokemonId ->
                    navController.navigate(PokemonDestinations.Detail.createRoute(pokemonId))
                }
            )
        }
        
        composable(
            PokemonDestinations.Detail.route,
            arguments = listOf(navArgument(PokemonDestinations.Detail.ARG_POKEMON_ID) { type = NavType.IntType })
        ) { backStackEntry ->
            val pokemonId = backStackEntry.arguments?.getInt(PokemonDestinations.Detail.ARG_POKEMON_ID) ?: 0
            PokemonDetailScreen(
                pokemonId = pokemonId,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}
