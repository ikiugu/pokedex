package com.ikiugu.oldmutual.presentation.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ikiugu.oldmutual.R
import com.ikiugu.oldmutual.presentation.ui.components.PokemonCard
import com.ikiugu.oldmutual.presentation.ui.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onPokemonClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        TopAppBar(
            title = {
                Text(
                    text = stringResource(R.string.pokedex),
                    fontWeight = FontWeight.Bold
                )
            }
        )
        
        OutlinedTextField(
            value = uiState.searchQuery,
            onValueChange = viewModel::onSearchQueryChanged,
            label = { Text(stringResource(R.string.search_pokemon)) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(R.dimen.content_padding)),
            singleLine = true
        )
        
        when {
            uiState.isLoading && uiState.pokemonList.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            
            uiState.error != null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(R.string.error, uiState.error.toString()),
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.content_padding)))
                        Button(onClick = viewModel::retry) {
                            Text(stringResource(R.string.retry))
                        }
                    }
                }
            }
            
            else -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(dimensionResource(R.dimen.content_padding)),
                    verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.content_padding)),
                    horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.content_padding))
                ) {
                    items(uiState.pokemonList) { pokemon ->
                        PokemonCard(
                            pokemon = pokemon,
                            onClick = onPokemonClick
                        )
                    }
                }
            }
        }
    }
}
