package com.ikiugu.pokedex.domain.entity

data class Pokemon(
    val id: Int,
    val name: String,
    val imageUrl: String,
    val types: List<String>,
    val baseExperience: Int,
    val height: Int,
    val weight: Int,
    val isLoaded: Boolean = false
)
