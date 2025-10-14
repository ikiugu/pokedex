package com.ikiugu.oldmutual.presentation.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
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
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
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
    val pokemonPagingItems: LazyPagingItems<com.ikiugu.oldmutual.domain.entity.Pokemon> = viewModel.pokemonPagingFlow.collectAsLazyPagingItems()

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
            uiState.searchQuery.isNotBlank() -> {
                // Show search results
                if (uiState.isLoading && uiState.searchResults.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else if (uiState.error != null) {
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
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(dimensionResource(R.dimen.content_padding)),
                        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.content_padding)),
                        horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.content_padding))
                    ) {
                        items(uiState.searchResults.size) { index ->
                            val pokemon = uiState.searchResults[index]
                            PokemonCard(
                                pokemon = pokemon,
                                onClick = onPokemonClick
                            )
                        }
                    }
                }
            }
            
            else -> {
                // Show paged Pokemon list
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(dimensionResource(R.dimen.content_padding)),
                    verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.content_padding)),
                    horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.content_padding))
                ) {
                    items(
                        count = pokemonPagingItems.itemCount,
                        key = { index -> pokemonPagingItems[index]?.id ?: index }
                    ) { index ->
                        val pokemon = pokemonPagingItems[index]
                        if (pokemon != null) {
                            PokemonCard(
                                pokemon = pokemon,
                                onClick = onPokemonClick
                            )
                        } else {
                            // Show placeholder while loading
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(1f),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                    }
                    
                    // Handle loading states
                    when (pokemonPagingItems.loadState.append) {
                        is LoadState.Loading -> {
                            item(span = { GridItemSpan(2) }) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(dimensionResource(R.dimen.content_padding)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                            }
                        }
                        is LoadState.Error -> {
                            item(span = { GridItemSpan(2) }) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(dimensionResource(R.dimen.content_padding)),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = stringResource(R.string.error, "Failed to load more Pokemon"),
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Spacer(modifier = Modifier.height(dimensionResource(R.dimen.content_padding)))
                                    Button(onClick = { pokemonPagingItems.retry() }) {
                                        Text(stringResource(R.string.retry))
                                    }
                                }
                            }
                        }
                        else -> {}
                    }
                }
            }
        }
    }
}
