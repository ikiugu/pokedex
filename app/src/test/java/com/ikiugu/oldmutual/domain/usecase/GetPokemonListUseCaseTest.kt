package com.ikiugu.oldmutual.domain.usecase

import androidx.paging.PagingData
import com.ikiugu.oldmutual.domain.entity.Pokemon
import com.ikiugu.oldmutual.domain.repository.PokemonRepository
import com.ikiugu.oldmutual.utils.CoroutineTest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.whenever
import org.mockito.kotlin.verify

@RunWith(MockitoJUnitRunner::class)
class GetPokemonListUseCaseTest : CoroutineTest() {

    @Mock
    private lateinit var pokemonRepository: PokemonRepository

    private lateinit var getPokemonListUseCase: GetPokemonListUseCase

    override fun setup() {
        super.setup()
        getPokemonListUseCase = GetPokemonListUseCase(pokemonRepository)
    }

    @Test
    fun shouldReturnPagingDataFromRepository() = runTest {
        val mockPagingData = PagingData.empty<Pokemon>()
        whenever(pokemonRepository.getPokemonList()).thenReturn(flowOf(mockPagingData))

        val result = getPokemonListUseCase()

        verify(pokemonRepository).getPokemonList()
        assertNotNull(result)
    }

    @Test
    fun shouldPropagateRepositoryErrors() = runTest {
        val exception = RuntimeException("Network error")
        whenever(pokemonRepository.getPokemonList()).thenThrow(exception)

        assertThrows(RuntimeException::class.java) {
            runBlocking {
                getPokemonListUseCase().collect { }
            }
        }
        
        verify(pokemonRepository).getPokemonList()
    }
}
