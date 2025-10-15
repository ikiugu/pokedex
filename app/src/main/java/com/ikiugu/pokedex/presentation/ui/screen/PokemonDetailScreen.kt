package com.ikiugu.pokedex.presentation.ui.screen

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.ikiugu.pokedex.R
import com.ikiugu.pokedex.presentation.ui.components.PokemonLoader
import com.ikiugu.pokedex.presentation.ui.utils.getContentPadding
import com.ikiugu.pokedex.presentation.ui.utils.getImageSize
import com.ikiugu.pokedex.presentation.ui.utils.getTypeColor
import com.ikiugu.pokedex.presentation.ui.viewmodel.PokemonDetailViewModel
import com.ikiugu.pokedex.ui.theme.Dimens
import com.ikiugu.pokedex.ui.theme.DetailGradientBottom
import com.ikiugu.pokedex.ui.theme.DetailGradientMid
import com.ikiugu.pokedex.ui.theme.DetailGradientTop
import com.ikiugu.pokedex.ui.theme.StatAttack
import com.ikiugu.pokedex.ui.theme.StatDefault
import com.ikiugu.pokedex.ui.theme.StatDefense
import com.ikiugu.pokedex.ui.theme.StatHp
import com.ikiugu.pokedex.ui.theme.StatSpeed


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PokemonDetailScreen(
    pokemonId: Int,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PokemonDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var scrollPosition by rememberSaveable { mutableIntStateOf(0) }

    val view = LocalView.current
    DisposableEffect(Unit) {
        val window = (view.context as android.app.Activity).window
        window.statusBarColor = Color.White.toArgb()
        WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true

        onDispose {
            window.statusBarColor = Color.White.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
        }
    }

    LaunchedEffect(pokemonId) {
        viewModel.loadPokemonDetail(pokemonId)
    }

    val headerColor = if (uiState.pokemonDetail != null) {
        val primaryType = uiState.pokemonDetail!!.pokemon.types.firstOrNull() ?: "normal"
        getTypeColor(primaryType)
    } else {
        Color.White
    }

    val animatedTopAppBarColor by animateColorAsState(
        targetValue = headerColor,
        animationSpec = tween(durationMillis = 500),
        label = "topAppBarColor"
    )

    val animatedTextColor by animateColorAsState(
        targetValue = if (headerColor != Color.White) Color.White else Color.Black,
        animationSpec = tween(durationMillis = 500),
        label = "textColor"
    )

    LaunchedEffect(headerColor) {
        if (headerColor != Color.White) {
            val window = (view.context as android.app.Activity).window
            window.statusBarColor = headerColor.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        TopAppBar(
            title = {
                Text(
                    text = stringResource(R.string.pokedex),
                    fontWeight = FontWeight.Bold,
                    color = animatedTextColor
                )
            },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back),
                        tint = animatedTextColor
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = animatedTopAppBarColor
            )
        )

        when {
            uiState.isLoading -> {
                PokemonLoader(
                    message = stringResource(R.string.loading_pokemon_details)
                )
            }

            uiState.error != null -> {
                ErrorContent(
                    error = uiState.error.toString(),
                    onRetry = { viewModel.retry(pokemonId) }
                )
            }

            uiState.pokemonDetail != null -> {
                PokemonDetailContent(
                    pokemonDetail = uiState.pokemonDetail!!,
                    headerColor = headerColor,
                    scrollPosition = scrollPosition,
                    onScrollPositionChanged = { scrollPosition = it },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Composable
private fun PokemonDetailContent(
    pokemonDetail: com.ikiugu.pokedex.domain.entity.PokemonDetail,
    headerColor: Color,
    scrollPosition: Int,
    onScrollPositionChanged: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState(scrollPosition)
    
    LaunchedEffect(scrollState) {
        snapshotFlow { scrollState.value }
            .collect { onScrollPositionChanged(it) }
    }

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE
    val imageSize = getImageSize(configuration) * if (isLandscape) 2.5f else 3.5f
    val headerHeight = if (isLandscape) Dimens.HeaderHeightLandscape else dimensionResource(R.dimen.carousel_height)

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(headerHeight)
                .background(headerColor),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = pokemonDetail.pokemon.imageUrl,
                contentDescription = pokemonDetail.pokemon.name,
                modifier = Modifier.size(imageSize),
                placeholder = painterResource(id = R.drawable.ic_pokemon_black_and_white),
                error = painterResource(id = R.drawable.ic_pokemon_black_and_white),
                contentScale = ContentScale.Crop
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            DetailGradientTop,
                            DetailGradientMid,
                            DetailGradientBottom
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
                    .padding(getContentPadding(configuration))
            ) {
                PokemonNameAndTypes(pokemon = pokemonDetail.pokemon)

                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_xlarge)))

                PhysicalAttributesRow(
                    weight = pokemonDetail.pokemon.weight,
                    height = pokemonDetail.pokemon.height
                )

                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_xlarge)))

                StatsSection(stats = pokemonDetail.stats)
            }
        }
    }
}

@Composable
private fun ErrorContent(
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
private fun PokemonNameAndTypes(
    pokemon: com.ikiugu.pokedex.domain.entity.Pokemon
) {
    Text(
        text = pokemon.name.replaceFirstChar { it.uppercase() },
        style = MaterialTheme.typography.headlineLarge,
        fontWeight = FontWeight.Bold,
        color = Color.White
    )

    Spacer(modifier = Modifier.height(dimensionResource(R.dimen.content_padding)))

    Row(
        horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_medium))
    ) {
        pokemon.types.forEach { type ->
            Surface(
                modifier = Modifier.clip(RoundedCornerShape(dimensionResource(R.dimen.type_corner_radius))),
                color = getTypeColor(type)
            ) {
                Text(
                    text = type.replaceFirstChar { it.uppercase() },
                    modifier = Modifier.padding(
                        horizontal = dimensionResource(R.dimen.type_padding_horizontal),
                        vertical = dimensionResource(R.dimen.type_padding_vertical)
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
private fun PhysicalAttributesRow(
    weight: Int,
    height: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        PhysicalAttributeItem(
            label = stringResource(R.string.weight),
            value = "${weight / 10.0} kg"
        )
        PhysicalAttributeItem(
            label = stringResource(R.string.height),
            value = "${height / 10.0} m"
        )
    }
}

@Composable
private fun StatsSection(
    stats: List<com.ikiugu.pokedex.domain.entity.PokemonStat>
) {
    Text(
        text = stringResource(R.string.base_stats),
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        color = Color.White
    )

    Spacer(modifier = Modifier.height(dimensionResource(R.dimen.content_padding)))

    stats.forEach { stat ->
        StatItem(
            statName = stat.name.replaceFirstChar { it.uppercase() },
            value = stat.baseStat,
            maxValue = 200
        )
        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_medium)))
    }
}

@Composable
private fun PhysicalAttributeItem(
    label: String,
    value: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White.copy(alpha = 0.7f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}

@Composable
private fun StatItem(
    statName: String,
    value: Int,
    maxValue: Int
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = statName,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        LinearProgressIndicator(
            progress = { value.toFloat() / maxValue },
            modifier = Modifier
                .fillMaxWidth()
                .height(dimensionResource(R.dimen.progress_bar_height))
                .clip(RoundedCornerShape(dimensionResource(R.dimen.progress_bar_corner_radius))),
            color = when (statName.lowercase()) {
                "hp" -> StatHp
                "attack" -> StatAttack
                "defense" -> StatDefense
                "speed" -> StatSpeed
                else -> StatDefault
            },
            trackColor = Color.White
        )
    }
}
