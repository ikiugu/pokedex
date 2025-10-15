package com.ikiugu.pokedex.presentation.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.ikiugu.pokedex.R
import com.ikiugu.pokedex.domain.entity.Pokemon
import com.ikiugu.pokedex.presentation.ui.utils.getCardPadding
import com.ikiugu.pokedex.presentation.ui.utils.getImageSize
import com.ikiugu.pokedex.ui.theme.PokemonCardContainer
import com.ikiugu.pokedex.ui.theme.PokedexTheme
import com.ikiugu.pokedex.ui.theme.TypeElectric
import com.ikiugu.pokedex.ui.theme.TypeGrass


@Composable
fun PokemonCard(
    pokemon: Pokemon,
    onClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE
    
    val imageSize = getImageSize(configuration)
    val cardPadding = getCardPadding(configuration)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick(pokemon.id) },
        shape = RoundedCornerShape(dimensionResource(R.dimen.card_corner_radius)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = PokemonCardContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(cardPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AsyncImage(
                model = pokemon.imageUrl,
                contentDescription = pokemon.name,
                modifier = Modifier
                    .size(imageSize)
                    .clip(RoundedCornerShape(dimensionResource(R.dimen.image_corner_radius))),
                placeholder = painterResource(id = R.drawable.ic_pokemon_black_and_white),
                error = painterResource(id = R.drawable.ic_pokemon_black_and_white),
                contentScale = ContentScale.Crop
            )
            
            val textSpacing = if (isLandscape) dimensionResource(R.dimen.spacing_small) else dimensionResource(R.dimen.spacing_medium)
            
            Spacer(modifier = Modifier.height(textSpacing))
            
            Text(
                text = pokemon.name.replaceFirstChar { it.uppercase() },
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            if (pokemon.types.isNotEmpty()) {
                Spacer(modifier = Modifier.height(textSpacing / 2))
                
                Text(
                    text = pokemon.types.first().replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PokemonCardPreview() {
    PokedexTheme {
        PokemonCard(
            pokemon = Pokemon(
                id = 1,
                name = "bulbasaur",
                imageUrl = "",
                types = listOf("grass", "poison"),
                baseExperience = 64,
                height = 7,
                weight = 69
            ),
            onClick = {}
        )
    }
}
