package com.ikiugu.oldmutual.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.ikiugu.oldmutual.data.local.dao.PokemonDao
import com.ikiugu.oldmutual.data.local.dao.PokemonDetailDao
import com.ikiugu.oldmutual.data.mapper.toDetail
import com.ikiugu.oldmutual.data.mapper.toDetailEntity
import com.ikiugu.oldmutual.data.mapper.toDomain
import com.ikiugu.oldmutual.data.mapper.toEntity
import com.ikiugu.oldmutual.data.local.entity.PokemonEntity
import com.ikiugu.oldmutual.data.paging.PokemonPagingSource
import com.ikiugu.oldmutual.data.remote.api.PokemonApiService
import com.ikiugu.oldmutual.data.util.retryWithBackoff
import com.ikiugu.oldmutual.domain.entity.Pokemon
import com.ikiugu.oldmutual.domain.entity.PokemonDetail
import com.ikiugu.oldmutual.domain.error.PokemonError
import com.ikiugu.oldmutual.domain.error.Result
import com.ikiugu.oldmutual.domain.error.toPokemonError
import com.ikiugu.oldmutual.domain.repository.PokemonRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.first
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PokemonRepositoryImpl @Inject constructor(
    private val apiService: PokemonApiService,
    private val pokemonDao: PokemonDao,
    private val pokemonDetailDao: PokemonDetailDao
) : PokemonRepository {

    override fun getPokemonList(): Flow<PagingData<Pokemon>> = Pager(
        config = PagingConfig(
            pageSize = 20,
            enablePlaceholders = false,
            prefetchDistance = 5
        ),
        pagingSourceFactory = {
            PokemonPagingSource(apiService, pokemonDao, pokemonDetailDao)
        }
    ).flow

    override fun getPokemonDetail(id: Int): Flow<Result<PokemonDetail>> = flow {
        emit(Result.Loading)
        
        try {
            val cachedDetail = pokemonDetailDao.getPokemonDetail(id)
            val cachedPokemon = pokemonDao.getPokemonById(id)
            
            if (cachedDetail != null && cachedPokemon != null) {
                emit(Result.Success(cachedDetail.toDomain(cachedPokemon.toDomain())))
            }
            
            val response = retryWithBackoff {
                Timber.d("Fetching Pokémon detail from API - id: $id")
                apiService.getPokemonDetail(id)
            }
            
            if (response.isSuccessful && response.body() != null) {
                val pokemonDetail = response.body()!!.toDetail()
                
                pokemonDao.insertPokemon(pokemonDetail.pokemon.toEntity())
                pokemonDetailDao.insertPokemonDetail(pokemonDetail.toDetailEntity())
                
                emit(Result.Success(pokemonDetail))
            } else {
                emit(Result.Error(PokemonError.NotFoundError))
            }
            
        } catch (e: Exception) {
            Timber.e(e, "Failed to fetch Pokémon detail")
            emit(Result.Error(e.toPokemonError()))
        }
    }.flowOn(Dispatchers.IO)

    override fun searchPokemon(query: String): Flow<Result<List<Pokemon>>> = flow {
        emit(Result.Loading)
        
        try {
            val localResults = pokemonDao.searchPokemon(query).first()
                .map { it.toDomain() }
            
            if (localResults.isNotEmpty()) {
                emit(Result.Success(localResults))
            }
            
            if (localResults.isEmpty()) {
                emit(Result.Success(emptyList()))
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to search Pokémon")
            emit(Result.Error(e.toPokemonError()))
        }
    }.flowOn(Dispatchers.IO)

    private fun buildImageUrl(id: Int): String {
        return "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/$id.png"
    }
}
