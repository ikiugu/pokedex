package com.ikiugu.pokedex.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.ikiugu.pokedex.data.local.dao.PokemonDao
import com.ikiugu.pokedex.data.local.dao.PokemonDetailDao
import com.ikiugu.pokedex.data.local.entity.PokemonEntity
import com.ikiugu.pokedex.data.mapper.toDetail
import com.ikiugu.pokedex.data.mapper.toDetailEntity
import com.ikiugu.pokedex.data.mapper.toDomain
import com.ikiugu.pokedex.data.mapper.toEntity
import com.ikiugu.pokedex.data.remote.api.PokemonApiService
import com.ikiugu.pokedex.data.util.retryWithBackoff
import com.ikiugu.pokedex.domain.entity.Pokemon
import timber.log.Timber
import javax.inject.Inject

class PokemonPagingSource @Inject constructor(
    private val apiService: PokemonApiService,
    private val pokemonDao: PokemonDao,
    private val pokemonDetailDao: PokemonDetailDao
) : PagingSource<Int, Pokemon>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Pokemon> {
        val page = params.key ?: 0
        val pageSize = params.loadSize.coerceAtMost(20)
        
        return try {
            // First, try to get cached data from local database
            val cachedPokemon = getCachedPokemon(page, pageSize)
            if (cachedPokemon.isNotEmpty()) {
                Timber.d("Returning cached Pokemon data for page $page")
                return LoadResult.Page(
                    data = cachedPokemon,
                    prevKey = if (page == 0) null else page - 1,
                    nextKey = page + 1
                )
            }
            
            // If no cached data, try to fetch from API
            val response = retryWithBackoff {
                Timber.d("Fetching Pokemon list from API - page: $page, pageSize: $pageSize")
                apiService.getPokemonList(limit = pageSize, offset = page * pageSize)
            }
            
            if (response.isSuccessful && response.body() != null) {
                val pokemonList = mutableListOf<Pokemon>()
                response.body()!!.results.forEachIndexed { index, result ->
                    val pokemonId = page * pageSize + index + 1
                    try {
                        val detailResponse = retryWithBackoff {
                            apiService.getPokemonDetail(pokemonId)
                        }
                        
                        if (detailResponse.isSuccessful && detailResponse.body() != null) {
                            val pokemonDetail = detailResponse.body()!!.toDetail()
                            pokemonList.add(pokemonDetail.pokemon)
                            pokemonDao.insertPokemon(pokemonDetail.pokemon.toEntity())
                            pokemonDetailDao.insertPokemonDetail(pokemonDetail.toDetailEntity())
                        } else {
                            val basicPokemon = Pokemon(
                                id = pokemonId,
                                name = result.name,
                                imageUrl = buildImageUrl(pokemonId),
                                types = emptyList(),
                                baseExperience = 0,
                                height = 0,
                                weight = 0,
                                isLoaded = false
                            )
                            pokemonList.add(basicPokemon)
                            pokemonDao.insertPokemon(basicPokemon.toEntity())
                        }
                    } catch (e: Exception) {
                        Timber.w(e, "Failed to fetch Pokemon detail for id: $pokemonId")
                        val basicPokemon = Pokemon(
                            id = pokemonId,
                            name = result.name,
                            imageUrl = buildImageUrl(pokemonId),
                            types = emptyList(),
                            baseExperience = 0,
                            height = 0,
                            weight = 0,
                            isLoaded = false
                        )
                        pokemonList.add(basicPokemon)
                        pokemonDao.insertPokemon(basicPokemon.toEntity())
                    }
                }
                
                val nextKey = if (pokemonList.isEmpty()) null else page + 1
                
                LoadResult.Page(
                    data = pokemonList,
                    prevKey = if (page == 0) null else page - 1,
                    nextKey = nextKey
                )
            } else {
                Timber.e("Failed to fetch Pokemon list from API")
                LoadResult.Error(Exception("Failed to fetch Pokemon list"))
            }
            
        } catch (e: Exception) {
            Timber.e(e, "Network error, trying to return cached data")
            // If network fails, try to return any cached data we have
            val fallbackPokemon = getCachedPokemon(0, pageSize)
            if (fallbackPokemon.isNotEmpty()) {
                Timber.d("Returning fallback cached Pokemon data")
                return LoadResult.Page(
                    data = fallbackPokemon,
                    prevKey = null,
                    nextKey = null
                )
            }
            LoadResult.Error(e)
        }
    }
    
    private suspend fun getCachedPokemon(page: Int, pageSize: Int): List<Pokemon> {
        return try {
            val offset = page * pageSize
            val cachedEntities = pokemonDao.getPokemonPaginated(pageSize, offset)
            
            val paginatedPokemon = cachedEntities.map { it.toDomain() }
            
            Timber.d("Found ${paginatedPokemon.size} cached Pokemon for page $page")
            paginatedPokemon
        } catch (e: Exception) {
            Timber.e(e, "Error getting cached Pokemon")
            emptyList()
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Pokemon>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
    
    private fun buildImageUrl(id: Int): String {
        return "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/$id.png"
    }
}
