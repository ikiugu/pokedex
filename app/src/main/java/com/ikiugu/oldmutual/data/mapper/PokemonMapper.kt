package com.ikiugu.oldmutual.data.mapper

import com.ikiugu.oldmutual.data.local.entity.PokemonEntity
import com.ikiugu.oldmutual.data.local.entity.PokemonDetailEntity
import com.ikiugu.oldmutual.data.remote.dto.PokemonResponse
import com.ikiugu.oldmutual.domain.entity.Pokemon
import com.ikiugu.oldmutual.domain.entity.PokemonDetail
import com.ikiugu.oldmutual.domain.entity.PokemonStat
import com.ikiugu.oldmutual.domain.entity.Ability
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
        imageUrl = buildImageUrl(id),
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

private fun buildImageUrl(id: Int): String {
    return "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/$id.png"
}
