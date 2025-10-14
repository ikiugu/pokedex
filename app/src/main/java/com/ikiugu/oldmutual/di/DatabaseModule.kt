package com.ikiugu.oldmutual.di

import android.content.Context
import androidx.room.Room
import com.ikiugu.oldmutual.data.local.dao.PokemonDao
import com.ikiugu.oldmutual.data.local.dao.PokemonDetailDao
import com.ikiugu.oldmutual.data.local.database.PokemonDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun providePokemonDatabase(@ApplicationContext context: Context): PokemonDatabase {
        return Room.databaseBuilder(
            context,
            PokemonDatabase::class.java,
            "pokemon_database"
        ).build()
    }

    @Provides
    fun providePokemonDao(database: PokemonDatabase): PokemonDao = database.pokemonDao()

    @Provides
    fun providePokemonDetailDao(database: PokemonDatabase): PokemonDetailDao = database.pokemonDetailDao()
}
