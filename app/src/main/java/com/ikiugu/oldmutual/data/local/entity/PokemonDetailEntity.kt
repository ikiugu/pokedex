package com.ikiugu.oldmutual.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pokemon_details")
data class PokemonDetailEntity(
    @PrimaryKey val pokemonId: Int,
    val stats: String,
    val abilities: String,
    val generation: String,
    val captureRate: Int,
    val lastUpdated: Long = System.currentTimeMillis()
)
