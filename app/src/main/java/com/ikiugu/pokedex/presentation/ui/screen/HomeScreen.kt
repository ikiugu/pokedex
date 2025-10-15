package com.ikiugu.pokedex.presentation.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.ikiugu.pokedex.R
import com.ikiugu.pokedex.domain.entity.Pokemon
import com.ikiugu.pokedex.presentation.ui.components.PokemonCard
import com.ikiugu.pokedex.presentation.ui.components.PokemonLoader
import com.ikiugu.pokedex.presentation.ui.utils.getContentPadding
import com.ikiugu.pokedex.presentation.ui.utils.getGridColumns
import com.ikiugu.pokedex.presentation.ui.utils.getGridColumnsSpanCount
import com.ikiugu.pokedex.presentation.ui.utils.getGridSpacing
import com.ikiugu.pokedex.presentation.ui.viewmodel.HomeViewModel
import com.ikiugu.pokedex.ui.theme.TypeElectric
import com.ikiugu.pokedex.ui.theme.PokemonCardContainer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onPokemonClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val pokemonPagingItems: LazyPagingItems<Pokemon> = viewModel.pokemonPagingFlow.collectAsLazyPagingItems()

    var searchQuery by rememberSaveable { mutableStateOf("") }
    var scrollPosition by rememberSaveable { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        if (uiState.searchQuery.isNotEmpty()) {
            searchQuery = uiState.searchQuery
        }
    }

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
            value = searchQuery,
            onValueChange = { newValue ->
                searchQuery = newValue
                viewModel.onSearchQueryChanged(newValue)
            },
            label = { Text(stringResource(R.string.search_pokemon)) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(R.dimen.content_padding)),
            singleLine = true
        )

        when {
            searchQuery.isNotBlank() -> {
                if (uiState.isLoading && uiState.searchResults.isEmpty()) {
                    PokemonLoader(
                        message = stringResource(R.string.searching_pokemon)
                    )
                } else if (uiState.error != null) {
                    SearchErrorContent(
                        error = uiState.error.toString(),
                        onRetry = viewModel::retry
                    )
                } else {
                    SearchResultsGrid(
                        results = uiState.searchResults,
                        onPokemonClick = onPokemonClick,
                        scrollPosition = scrollPosition,
                        onScrollPositionChanged = { scrollPosition = it }
                    )
                }
            }

            else -> {
                when (pokemonPagingItems.loadState.refresh) {
                    is LoadState.Loading -> {
                        PokemonLoader(
                            message = stringResource(R.string.loading_pokemon)
                        )
                    }

                    is LoadState.Error -> {
                        SearchErrorContent(
                            error = stringResource(R.string.failed_to_load_pokemon_list),
                            onRetry = { pokemonPagingItems.retry() }
                        )
                    }

                    else -> {
                        PagedPokemonGrid(
                            pokemonPagingItems = pokemonPagingItems,
                            onPokemonClick = onPokemonClick,
                            scrollPosition = scrollPosition,
                            onScrollPositionChanged = { scrollPosition = it }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchErrorContent(
    error: String,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.error, error),
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.content_padding)))
            Button(onClick = onRetry) {
                Text(stringResource(R.string.retry))
            }
        }
    }
}

@Composable
private fun SearchResultsGrid(
    results: List<Pokemon>,
    onPokemonClick: (Int) -> Unit,
    scrollPosition: Int,
    onScrollPositionChanged: (Int) -> Unit
) {
    val configuration = LocalConfiguration.current
    val gridSpacing = getGridSpacing(configuration)
    val contentPadding = getContentPadding(configuration)
    val gridState = rememberLazyGridState(scrollPosition)

    LaunchedEffect(gridState) {
        snapshotFlow { gridState.firstVisibleItemIndex }
            .collect { onScrollPositionChanged(it) }
    }

    LazyVerticalGrid(
        columns = getGridColumns(configuration),
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(gridSpacing),
        horizontalArrangement = Arrangement.spacedBy(gridSpacing),
        state = gridState
    ) {
        items(count = results.size, key = { index -> results[index].id }) { index ->
            val pokemon = results[index]
            PokemonCard(
                pokemon = pokemon,
                onClick = onPokemonClick
            )
        }
    }
}

@Composable
private fun PagedPokemonGrid(
    pokemonPagingItems: LazyPagingItems<Pokemon>,
    onPokemonClick: (Int) -> Unit,
    scrollPosition: Int,
    onScrollPositionChanged: (Int) -> Unit
) {
    val configuration = LocalConfiguration.current
    val gridSpacing = getGridSpacing(configuration)
    val contentPadding = getContentPadding(configuration)
    val spanCount = getGridColumnsSpanCount(configuration)
    val gridState = rememberLazyGridState(scrollPosition)

    LaunchedEffect(gridState) {
        snapshotFlow { gridState.firstVisibleItemIndex }
            .collect { onScrollPositionChanged(it) }
    }

    LazyVerticalGrid(
        columns = getGridColumns(configuration),
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(gridSpacing),
        horizontalArrangement = Arrangement.spacedBy(gridSpacing),
        state = gridState
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
                LoadingPlaceholder()
            }
        }

        when (pokemonPagingItems.loadState.append) {
            is LoadState.Loading -> {
                item(span = { GridItemSpan(spanCount) }) {
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
                item(span = { GridItemSpan(spanCount) }) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(dimensionResource(R.dimen.content_padding)),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(R.string.error, stringResource(R.string.failed_to_load_more_pokemon)),
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

@Composable
private fun LoadingPlaceholder() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .background(
                PokemonCardContainer,
                RoundedCornerShape(dimensionResource(R.dimen.card_corner_radius))
            ),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(dimensionResource(R.dimen.placeholder_icon_size)),
            color = TypeElectric
        )
    }
}
