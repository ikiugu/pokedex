package com.ikiugu.pokedex.domain.usecase

import com.ikiugu.pokedex.domain.entity.Pokemon
import com.ikiugu.pokedex.domain.error.PokemonError
import com.ikiugu.pokedex.domain.error.Result
import com.ikiugu.pokedex.domain.repository.PokemonRepository
import com.ikiugu.pokedex.utils.CoroutineTest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@RunWith(MockitoJUnitRunner::class)
class SearchPokemonUseCaseTest : CoroutineTest() {

    @Mock
    private lateinit var pokemonRepository: PokemonRepository

    @Mock
    private lateinit var searchPokemonUseCase: SearchPokemonUseCase

    override fun setup() {
        super.setup()
        searchPokemonUseCase = SearchPokemonUseCase(pokemonRepository)
    }

    @Test
    fun shouldReturnSuccessResultOnRepositorySuccess() = runTest {
        val mockPokemon = listOf(
            Pokemon(1, "bulbasaur", "image_url", listOf("grass"), 64, 7, 69),
            Pokemon(2, "ivysaur", "image_url", listOf("grass"), 142, 10, 130)
        )
        whenever(pokemonRepository.searchPokemon("bulbasaur")).thenReturn(flowOf(Result.Success(mockPokemon)))

        val result = searchPokemonUseCase("bulbasaur")

        verify(pokemonRepository).searchPokemon("bulbasaur")
        result.collect { result ->
            assertTrue(result is Result.Success)
            assertEquals(mockPokemon, (result as Result.Success).data)
        }
    }

    @Test
    fun shouldReturnErrorResultOnRepositoryError() = runTest {
        val error = PokemonError.NetworkError
        whenever(pokemonRepository.searchPokemon("invalid")).thenReturn(flowOf(Result.Error(error)))

        val result = searchPokemonUseCase("invalid")

        verify(pokemonRepository).searchPokemon("invalid")
        result.collect { result ->
            assertTrue(result is Result.Error)
            assertEquals(error, (result as Result.Error).exception)
        }
    }

    @Test
    fun shouldReturnEmptyListWhenNoPokemonFound() = runTest {
        val emptyList = emptyList<Pokemon>()
        whenever(pokemonRepository.searchPokemon("nonexistent")).thenReturn(flowOf(Result.Success(emptyList)))

        val result = searchPokemonUseCase("nonexistent")

        verify(pokemonRepository).searchPokemon("nonexistent")
        result.collect { result ->
            assertTrue(result is Result.Success)
            assertTrue((result as Result.Success).data.isEmpty())
        }
    }

    @Test
    fun shouldPropagateRepositoryExceptions() = runTest {
        val exception = RuntimeException("Database error")
        whenever(pokemonRepository.searchPokemon("test")).thenThrow(exception)

        assertThrows(RuntimeException::class.java) {
            runBlocking {
                searchPokemonUseCase("test").collect { }
            }
        }
        
        verify(pokemonRepository).searchPokemon("test")
    }

    @Test
    fun shouldHandleEmptySearchQuery() = runTest {
        val emptyList = emptyList<Pokemon>()
        whenever(pokemonRepository.searchPokemon("")).thenReturn(flowOf(Result.Success(emptyList)))

        val result = searchPokemonUseCase("")

        verify(pokemonRepository).searchPokemon("")
        result.collect { result ->
            assertTrue(result is Result.Success)
            assertTrue((result as Result.Success).data.isEmpty())
        }
    }
}
