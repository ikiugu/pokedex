package com.ikiugu.oldmutual.data.local.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import com.ikiugu.oldmutual.data.local.dao.PokemonDao
import com.ikiugu.oldmutual.data.local.dao.PokemonDetailDao
import com.ikiugu.oldmutual.data.local.entity.PokemonEntity
import com.ikiugu.oldmutual.data.local.entity.PokemonDetailEntity

@Database(
    entities = [PokemonEntity::class, PokemonDetailEntity::class],
    version = 1,
    exportSchema = false
)
abstract class PokemonDatabase : RoomDatabase() {
    abstract fun pokemonDao(): PokemonDao
    abstract fun pokemonDetailDao(): PokemonDetailDao
}
