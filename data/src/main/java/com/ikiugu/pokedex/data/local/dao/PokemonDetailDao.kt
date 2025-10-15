package com.ikiugu.pokedex.data.local.dao

import androidx.room.*
import com.ikiugu.pokedex.data.local.entity.PokemonDetailEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PokemonDetailDao {
    @Query("SELECT * FROM pokemon_details WHERE pokemonId = :pokemonId")
    suspend fun getPokemonDetail(pokemonId: Int): PokemonDetailEntity?

    @Query("SELECT * FROM pokemon_details WHERE pokemonId = :pokemonId")
    fun getPokemonDetailFlow(pokemonId: Int): Flow<PokemonDetailEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPokemonDetail(detail: PokemonDetailEntity)

    @Update
    suspend fun updatePokemonDetail(detail: PokemonDetailEntity)

    @Query("DELETE FROM pokemon_details WHERE pokemonId = :pokemonId")
    suspend fun deletePokemonDetail(pokemonId: Int)

    @Query("DELETE FROM pokemon_details")
    suspend fun deleteAllPokemonDetails()
}
