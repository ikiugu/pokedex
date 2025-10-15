package com.ikiugu.pokedex.domain.entity

data class PokemonDetail(
    val pokemon: Pokemon,
    val stats: List<PokemonStat>,
    val abilities: List<Ability>,
    val generation: String,
    val captureRate: Int
)
