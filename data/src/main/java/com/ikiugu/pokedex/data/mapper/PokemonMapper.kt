package com.ikiugu.pokedex.data.mapper

import com.ikiugu.pokedex.data.local.entity.PokemonEntity
import com.ikiugu.pokedex.data.local.entity.PokemonDetailEntity
import com.ikiugu.pokedex.data.remote.dto.PokemonResponse
import com.ikiugu.pokedex.domain.entity.Pokemon
import com.ikiugu.pokedex.domain.entity.PokemonDetail
import com.ikiugu.pokedex.domain.entity.PokemonStat
import com.ikiugu.pokedex.domain.entity.Ability
import com.ikiugu.pokedex.data.remote.api.ImageUrlBuilder
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

fun PokemonEntity.toDomain(): Pokemon {
    return Pokemon(
        id = id,
        name = name,
        imageUrl = imageUrl,
        types = Gson().fromJson(types, object : TypeToken<List<String>>() {}.type) ?: emptyList(),
        baseExperience = baseExperience,
        height = height,
        weight = weight,
        isLoaded = isLoaded
    )
}

fun PokemonResponse.toDomain(): Pokemon {
    return Pokemon(
        id = id,
        name = name,
        imageUrl = ImageUrlBuilder.officialArtwork(id),
        types = types.map { it.type.name },
        baseExperience = baseExperience,
        height = height,
        weight = weight,
        isLoaded = true
    )
}

fun PokemonResponse.toDetail(): PokemonDetail {
    return PokemonDetail(
        pokemon = toDomain(),
        stats = stats.map { stat ->
            PokemonStat(
                name = stat.stat.name,
                baseStat = stat.baseStat,
                effort = stat.effort
            )
        },
        abilities = abilities.map { ability ->
            Ability(
                name = ability.ability.name,
                isHidden = ability.isHidden
            )
        },
        generation = "Generation I",
        captureRate = 45
    )
}

fun PokemonDetailEntity.toDomain(pokemon: Pokemon): PokemonDetail {
    val gson = Gson()
    val statsType = object : TypeToken<List<PokemonStat>>() {}.type
    val abilitiesType = object : TypeToken<List<Ability>>() {}.type
    
    return PokemonDetail(
        pokemon = pokemon,
        stats = gson.fromJson(stats, statsType) ?: emptyList(),
        abilities = gson.fromJson(abilities, abilitiesType) ?: emptyList(),
        generation = generation,
        captureRate = captureRate
    )
}

fun Pokemon.toEntity(): PokemonEntity {
    return PokemonEntity(
        id = id,
        name = name,
        imageUrl = imageUrl,
        types = Gson().toJson(types),
        baseExperience = baseExperience,
        height = height,
        weight = weight,
        isLoaded = isLoaded
    )
}

fun PokemonDetail.toDetailEntity(): PokemonDetailEntity {
    val gson = Gson()
    return PokemonDetailEntity(
        pokemonId = pokemon.id,
        stats = gson.toJson(stats),
        abilities = gson.toJson(abilities),
        generation = generation,
        captureRate = captureRate
    )
}

// Image URL construction centralized in ImageUrlBuilder
