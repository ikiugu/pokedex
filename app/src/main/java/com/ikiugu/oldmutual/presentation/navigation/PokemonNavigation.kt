package com.ikiugu.oldmutual.presentation.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ikiugu.oldmutual.presentation.ui.screen.HomeScreen
import com.ikiugu.oldmutual.presentation.ui.screen.PokemonDetailScreen

@Composable
fun PokemonNavigation(
    paddingValues: PaddingValues,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            HomeScreen(
                onPokemonClick = { pokemonId ->
                    navController.navigate("detail/$pokemonId")
                }
            )
        }
        
        composable("detail/{pokemonId}") { backStackEntry ->
            val pokemonId = backStackEntry.arguments?.getString("pokemonId")?.toIntOrNull() ?: 0
            PokemonDetailScreen(
                pokemonId = pokemonId,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}
