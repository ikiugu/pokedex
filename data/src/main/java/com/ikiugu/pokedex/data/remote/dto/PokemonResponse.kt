package com.ikiugu.pokedex.data.remote.dto

import com.google.gson.annotations.SerializedName

data class PokemonResponse(
    val id: Int,
    val name: String,
    val sprites: PokemonSprites,
    val types: List<PokemonType>,
    val stats: List<PokemonStat>,
    @SerializedName("base_experience")
    val baseExperience: Int,
    val height: Int,
    val weight: Int,
    val abilities: List<PokemonAbility>
)

data class PokemonSprites(
    @SerializedName("front_default")
    val frontDefault: String,
    @SerializedName("front_shiny")
    val frontShiny: String,
    val other: PokemonSpritesOther?
)

data class PokemonSpritesOther(
    @SerializedName("official-artwork")
    val officialArtwork: PokemonSpritesOfficialArtwork?
)

data class PokemonSpritesOfficialArtwork(
    @SerializedName("front_default")
    val frontDefault: String
)

data class PokemonType(
    val slot: Int,
    val type: TypeInfo
)

data class TypeInfo(
    val name: String,
    val url: String
)

data class PokemonStat(
    @SerializedName("base_stat")
    val baseStat: Int,
    val effort: Int,
    val stat: StatInfo
)

data class StatInfo(
    val name: String,
    val url: String
)

data class PokemonAbility(
    val ability: AbilityInfo,
    @SerializedName("is_hidden")
    val isHidden: Boolean,
    val slot: Int
)

data class AbilityInfo(
    val name: String,
    val url: String
)

data class PokemonListResponse(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<PokemonListItem>
)

data class PokemonListItem(
    val name: String,
    val url: String
)
