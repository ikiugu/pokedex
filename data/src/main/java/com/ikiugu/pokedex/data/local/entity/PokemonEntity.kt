package com.ikiugu.pokedex.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pokemon")
data class PokemonEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val imageUrl: String,
    val types: String,
    val baseExperience: Int,
    val height: Int,
    val weight: Int,
    val isLoaded: Boolean = false,
    val lastUpdated: Long = System.currentTimeMillis()
)
