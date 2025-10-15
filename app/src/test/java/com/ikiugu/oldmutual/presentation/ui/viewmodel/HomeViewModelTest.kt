package com.ikiugu.oldmutual.presentation.ui.viewmodel

import androidx.paging.PagingData
import com.ikiugu.oldmutual.domain.entity.Pokemon
import com.ikiugu.oldmutual.domain.error.PokemonError
import com.ikiugu.oldmutual.domain.error.Result
import com.ikiugu.oldmutual.domain.usecase.GetPokemonListUseCase
import com.ikiugu.oldmutual.domain.usecase.SearchPokemonUseCase
import com.ikiugu.oldmutual.utils.CoroutineTest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@RunWith(MockitoJUnitRunner::class)
class HomeViewModelTest : CoroutineTest() {

    @Mock
    private lateinit var getPokemonListUseCase: GetPokemonListUseCase

    @Mock
    private lateinit var searchPokemonUseCase: SearchPokemonUseCase

    private lateinit var viewModel: HomeViewModel

    override fun setup() {
        super.setup()
        whenever(getPokemonListUseCase()).thenReturn(flowOf(PagingData.empty()))
        viewModel = HomeViewModel(getPokemonListUseCase, searchPokemonUseCase)
    }

    @Test
    fun shouldHaveEmptyInitialState() = runTest {
        val state = viewModel.uiState.value
        assertTrue(state.pokemonList.isEmpty())
        assertTrue(state.searchResults.isEmpty())
        assertFalse(state.isLoading)
        assertFalse(state.isRefreshing)
        assertNull(state.error)
        assertTrue(state.searchQuery.isEmpty())
        assertFalse(state.hasReachedEnd)
    }

    @Test
    fun shouldClearResultsWhenSearchQueryIsEmpty() = runTest {
        viewModel.onSearchQueryChanged("")

        val state = viewModel.uiState.value
        assertTrue(state.searchQuery.isEmpty())
        assertTrue(state.searchResults.isEmpty())
        assertFalse(state.isLoading)
    }

    @Test
    fun shouldPerformSearchWhenQueryIsProvided() = runTest {
        val mockPokemon = Pokemon(1, "bulbasaur", "image_url", listOf("grass"), 64, 7, 69)
        val searchResults = listOf(mockPokemon)
        
        whenever(searchPokemonUseCase("bulbasaur")).thenReturn(flowOf(Result.Success(searchResults)))

        viewModel.onSearchQueryChanged("bulbasaur")

        verify(searchPokemonUseCase).invoke("bulbasaur")
        
        val state = viewModel.uiState.value
        assertEquals("bulbasaur", state.searchQuery)
        assertEquals(searchResults, state.searchResults)
        assertFalse(state.isLoading)
        assertNull(state.error)
    }

    @Test
    fun shouldEmitErrorWhenSearchFails() = runTest {
        val error = PokemonError.NetworkError
        
        whenever(searchPokemonUseCase("invalid")).thenReturn(flowOf(Result.Error(error)))

        viewModel.onSearchQueryChanged("invalid")

        val state = viewModel.uiState.value
        assertEquals("invalid", state.searchQuery)
        assertTrue(state.searchResults.isEmpty())
        assertFalse(state.isLoading)
        assertEquals(error, state.error)
    }

    @Test
    fun shouldRetrySearchWhenRetryIsCalled() = runTest {
        val mockPokemon = Pokemon(1, "bulbasaur", "image_url", listOf("grass"), 64, 7, 69)
        val searchResults = listOf(mockPokemon)
        
        whenever(searchPokemonUseCase("bulbasaur")).thenReturn(flowOf(Result.Success(searchResults)))
        
        viewModel.onSearchQueryChanged("bulbasaur")
        viewModel.retry()

        verify(searchPokemonUseCase, times(2)).invoke("bulbasaur")
        
        val state = viewModel.uiState.value
        assertNull(state.error)
    }

    @Test
    fun shouldCallUseCaseForPagingFlow() = runTest {
        verify(getPokemonListUseCase).invoke()
        assertNotNull(viewModel.pokemonPagingFlow)
    }
}
