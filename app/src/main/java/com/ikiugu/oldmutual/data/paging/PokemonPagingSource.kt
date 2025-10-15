package com.ikiugu.oldmutual.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.ikiugu.oldmutual.data.local.dao.PokemonDao
import com.ikiugu.oldmutual.data.local.dao.PokemonDetailDao
import com.ikiugu.oldmutual.data.local.entity.PokemonEntity
import com.ikiugu.oldmutual.data.mapper.toDetail
import com.ikiugu.oldmutual.data.mapper.toDetailEntity
import com.ikiugu.oldmutual.data.mapper.toEntity
import com.ikiugu.oldmutual.data.remote.api.PokemonApiService
import com.ikiugu.oldmutual.data.util.retryWithBackoff
import com.ikiugu.oldmutual.domain.entity.Pokemon
import timber.log.Timber
import javax.inject.Inject

class PokemonPagingSource @Inject constructor(
    private val apiService: PokemonApiService,
    private val pokemonDao: PokemonDao,
    private val pokemonDetailDao: PokemonDetailDao
) : PagingSource<Int, Pokemon>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Pokemon> {
        return try {
            val page = params.key ?: 0
            val pageSize = params.loadSize.coerceAtMost(20)
            val response = retryWithBackoff {
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
                LoadResult.Error(Exception("Failed to fetch Pokemon list"))
            }
            
        } catch (e: Exception) {
            LoadResult.Error(e)
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
