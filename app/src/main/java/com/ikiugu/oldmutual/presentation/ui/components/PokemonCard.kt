package com.ikiugu.oldmutual.presentation.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.ikiugu.oldmutual.R
import com.ikiugu.oldmutual.domain.entity.Pokemon
import com.ikiugu.oldmutual.ui.theme.OldMutualTheme

@Composable
fun PokemonCard(
    pokemon: Pokemon,
    onClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick(pokemon.id) },
        shape = RoundedCornerShape(dimensionResource(R.dimen.card_corner_radius)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(R.dimen.card_padding)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AsyncImage(
                model = pokemon.imageUrl,
                contentDescription = pokemon.name,
                modifier = Modifier
                    .size(dimensionResource(R.dimen.pokemon_image_size))
                    .clip(RoundedCornerShape(dimensionResource(R.dimen.image_corner_radius))),
                error = null
            )
            
            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_medium)))
            
            Text(
                text = pokemon.name.replaceFirstChar { it.uppercase() },
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            if (pokemon.types.isNotEmpty()) {
                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_small)))
                
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
    OldMutualTheme {
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
