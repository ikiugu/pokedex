package com.ikiugu.pokedex.data.di

import android.content.Context
import androidx.room.Room
import com.ikiugu.pokedex.data.local.dao.PokemonDao
import com.ikiugu.pokedex.data.local.dao.PokemonDetailDao
import com.ikiugu.pokedex.data.local.database.PokemonDatabase
import com.ikiugu.pokedex.data.local.database.DbConfig
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
        // Optional SQLCipher hook:
        // val passphrase = SQLiteDatabase.getBytes("your-secure-passphrase".toCharArray())
        // val factory = SupportFactory(passphrase)
        // .openHelperFactory(factory)

        return Room.databaseBuilder(
            context,
            PokemonDatabase::class.java,
            DbConfig.NAME
        ).build()
    }

    @Provides
    fun providePokemonDao(database: PokemonDatabase): PokemonDao = database.pokemonDao()

    @Provides
    fun providePokemonDetailDao(database: PokemonDatabase): PokemonDetailDao = database.pokemonDetailDao()
}
