package com.ikiugu.oldmutual.presentation.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.ikiugu.oldmutual.R
import com.ikiugu.oldmutual.presentation.ui.viewmodel.PokemonDetailViewModel
import com.ikiugu.oldmutual.ui.theme.PokemonBlue
import com.ikiugu.oldmutual.ui.theme.PokemonGreen
import com.ikiugu.oldmutual.ui.theme.StatAttack
import com.ikiugu.oldmutual.ui.theme.StatDefault
import com.ikiugu.oldmutual.ui.theme.StatDefense
import com.ikiugu.oldmutual.ui.theme.StatHp
import com.ikiugu.oldmutual.ui.theme.StatSpeed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PokemonDetailScreen(
    pokemonId: Int,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PokemonDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    LaunchedEffect(pokemonId) {
        viewModel.loadPokemonDetail(pokemonId)
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
            },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
                }
            }
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
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Composable
private fun PokemonDetailContent(
    pokemonDetail: com.ikiugu.oldmutual.domain.entity.PokemonDetail,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    
    Column(
        modifier = modifier.verticalScroll(scrollState)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(dimensionResource(R.dimen.header_height))
                .background(
                    color = PokemonBlue,
                    shape = RoundedCornerShape(
                        bottomStart = dimensionResource(R.dimen.header_corner_radius),
                        bottomEnd = dimensionResource(R.dimen.header_corner_radius)
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = pokemonDetail.pokemon.imageUrl,
                contentDescription = pokemonDetail.pokemon.name,
                modifier = Modifier.size(dimensionResource(R.dimen.detail_image_size))
            )
        }
        
        Column(
            modifier = Modifier
                .fillMaxWidth()
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
                        color = PokemonGreen
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
                    maxValue = 300
                )
                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_medium)))
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
            Text(
                text = "$value/$maxValue",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.7f)
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
            }
        )
    }
}
