package com.ikiugu.pokedex.data.local.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import com.ikiugu.pokedex.data.local.dao.PokemonDao
import com.ikiugu.pokedex.data.local.dao.PokemonDetailDao
import com.ikiugu.pokedex.data.local.entity.PokemonEntity
import com.ikiugu.pokedex.data.local.entity.PokemonDetailEntity

@Database(
    entities = [PokemonEntity::class, PokemonDetailEntity::class],
    version = 1,
    exportSchema = false
)
abstract class PokemonDatabase : RoomDatabase() {
    abstract fun pokemonDao(): PokemonDao
    abstract fun pokemonDetailDao(): PokemonDetailDao
}
