package com.ikiugu.oldmutual.domain.usecase

import com.ikiugu.oldmutual.domain.entity.Ability
import com.ikiugu.oldmutual.domain.entity.Pokemon
import com.ikiugu.oldmutual.domain.entity.PokemonDetail
import com.ikiugu.oldmutual.domain.entity.PokemonStat
import com.ikiugu.oldmutual.domain.error.PokemonError
import com.ikiugu.oldmutual.domain.error.Result
import com.ikiugu.oldmutual.domain.repository.PokemonRepository
import com.ikiugu.oldmutual.utils.CoroutineTest
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
class GetPokemonDetailUseCaseTest : CoroutineTest() {

    @Mock
    private lateinit var pokemonRepository: PokemonRepository

    private lateinit var getPokemonDetailUseCase: GetPokemonDetailUseCase

    override fun setup() {
        super.setup()
        getPokemonDetailUseCase = GetPokemonDetailUseCase(pokemonRepository)
    }

    @Test
    fun shouldReturnSuccessResultOnRepositorySuccess() = runTest {
        val mockPokemon = Pokemon(1, "bulbasaur", "image_url", listOf("grass"), 64, 7, 69)
        val mockStats = listOf(
            PokemonStat("hp", 45, 0),
            PokemonStat("attack", 49, 0)
        )
        val mockAbilities = listOf(
            Ability("overgrow", false),
            Ability("chlorophyll", true)
        )
        val mockDetail = PokemonDetail(
            pokemon = mockPokemon,
            stats = mockStats,
            abilities = mockAbilities,
            generation = "Generation I",
            captureRate = 45
        )

        whenever(pokemonRepository.getPokemonDetail(1)).thenReturn(flowOf(Result.Success(mockDetail)))

        val result = getPokemonDetailUseCase(1)

        verify(pokemonRepository).getPokemonDetail(1)
        result.collect { result ->
            assertTrue(result is Result.Success)
            assertEquals(mockDetail, (result as Result.Success).data)
        }
    }

    @Test
    fun shouldReturnErrorResultOnRepositoryError() = runTest {
        val error = PokemonError.NetworkError
        whenever(pokemonRepository.getPokemonDetail(1)).thenReturn(flowOf(Result.Error(error)))

        val result = getPokemonDetailUseCase(1)

        verify(pokemonRepository).getPokemonDetail(1)
        result.collect { result ->
            assertTrue(result is Result.Error)
            assertEquals(error, (result as Result.Error).exception)
        }
    }

    @Test
    fun shouldReturnLoadingResultOnRepositoryLoading() = runTest {
        whenever(pokemonRepository.getPokemonDetail(1)).thenReturn(flowOf(Result.Loading))

        val result = getPokemonDetailUseCase(1)

        verify(pokemonRepository).getPokemonDetail(1)
        result.collect { result ->
            assertTrue(result is Result.Loading)
        }
    }

    @Test
    fun shouldPropagateRepositoryExceptions() = runTest {
        val exception = RuntimeException("Database error")
        whenever(pokemonRepository.getPokemonDetail(1)).thenThrow(exception)

        assertThrows(RuntimeException::class.java) {
            runBlocking {
                getPokemonDetailUseCase(1).collect { }
            }
        }
        
        verify(pokemonRepository).getPokemonDetail(1)
    }
}
