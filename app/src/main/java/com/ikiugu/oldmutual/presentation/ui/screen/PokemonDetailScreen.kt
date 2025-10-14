package com.ikiugu.oldmutual.presentation.ui.screen

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.ikiugu.oldmutual.R
import com.ikiugu.oldmutual.presentation.ui.viewmodel.PokemonDetailViewModel
import com.ikiugu.oldmutual.ui.theme.*

private fun getTypeColor(type: String): Color {
    return when (type.lowercase()) {
        "normal" -> TypeNormal
        "fire" -> TypeFire
        "water" -> TypeWater
        "electric" -> TypeElectric
        "grass" -> TypeGrass
        "ice" -> TypeIce
        "fighting" -> TypeFighting
        "poison" -> TypePoison
        "ground" -> TypeGround
        "flying" -> TypeFlying
        "psychic" -> TypePsychic
        "bug" -> TypeBug
        "rock" -> TypeRock
        "ghost" -> TypeGhost
        "dragon" -> TypeDragon
        "dark" -> TypeDark
        "steel" -> TypeSteel
        "fairy" -> TypeFairy
        else -> TypeNormal
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PokemonDetailScreen(
    pokemonId: Int,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PokemonDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Set status bar color immediately when composable loads
    val view = LocalView.current
    DisposableEffect(Unit) {
        val window = (view.context as android.app.Activity).window
        window.statusBarColor = Color.White.toArgb()
        WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true

        onDispose {
            // Reset to default when leaving the screen
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
        null
    }

    // Animated colors for smooth transitions
    val animatedTopAppBarColor by animateColorAsState(
        targetValue = headerColor ?: Color.White,
        animationSpec = tween(durationMillis = 500),
        label = "topAppBarColor"
    )

    val animatedTextColor by animateColorAsState(
        targetValue = if (headerColor != null) Color.White else Color.Black,
        animationSpec = tween(durationMillis = 500),
        label = "textColor"
    )

    LaunchedEffect(uiState.pokemonDetail) {
        if (uiState.pokemonDetail != null) {
            val primaryType = uiState.pokemonDetail!!.pokemon.types.firstOrNull() ?: "normal"
            val pokemonHeaderColor = getTypeColor(primaryType)
            val window = (view.context as android.app.Activity).window

            // Animate the color transition
            val startColor = Color.White
            val steps = 30
            val stepDuration = 16L // ~60fps

            for (i in 0..steps) {
                val progress = i.toFloat() / steps
                val animatedColor = Color(
                    red = startColor.red + (pokemonHeaderColor.red - startColor.red) * progress,
                    green = startColor.green + (pokemonHeaderColor.green - startColor.green) * progress,
                    blue = startColor.blue + (pokemonHeaderColor.blue - startColor.blue) * progress,
                    alpha = 1f
                )
                window.statusBarColor = animatedColor.toArgb()
                kotlinx.coroutines.delay(stepDuration)
            }

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
                        Button(onClick = { viewModel.retry(pokemonId) }) {
                            Text(stringResource(R.string.retry))
                        }
                    }
                }
            }

            uiState.pokemonDetail != null -> {
                PokemonDetailContent(
                    pokemonDetail = uiState.pokemonDetail!!,
                    headerColor = headerColor,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Composable
private fun PokemonDetailContent(
    pokemonDetail: com.ikiugu.oldmutual.domain.entity.PokemonDetail,
    headerColor: Color?,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val finalHeaderColor = headerColor ?: getTypeColor(pokemonDetail.pokemon.types.firstOrNull() ?: "normal")

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(dimensionResource(R.dimen.header_height))
                .background(finalHeaderColor),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = pokemonDetail.pokemon.imageUrl,
                contentDescription = pokemonDetail.pokemon.name,
                modifier = Modifier.size(dimensionResource(R.dimen.detail_image_size))
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(Color(0xFF1A1A1A))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
                    .padding(dimensionResource(R.dimen.content_padding))
            ) {
                Text(
                    text = pokemonDetail.pokemon.name.replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.content_padding)))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_medium))
                ) {
                    pokemonDetail.pokemon.types.forEach { type ->
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

                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_xlarge)))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    PhysicalAttributeItem(
                        label = stringResource(R.string.weight),
                        value = "${pokemonDetail.pokemon.weight / 10.0} kg"
                    )
                    PhysicalAttributeItem(
                        label = stringResource(R.string.height),
                        value = "${pokemonDetail.pokemon.height / 10.0} m"
                    )
                }

                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_xlarge)))

                Text(
                    text = stringResource(R.string.base_stats),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.content_padding)))

                pokemonDetail.stats.forEach { stat ->
                    StatItem(
                        statName = stat.name.replaceFirstChar { it.uppercase() },
                        value = stat.baseStat,
                        maxValue = 150
                    )
                    Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_medium)))
                }
            }
        }
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
