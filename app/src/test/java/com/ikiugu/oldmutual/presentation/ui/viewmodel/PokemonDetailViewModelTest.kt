package com.ikiugu.oldmutual.presentation.ui.viewmodel

import com.ikiugu.oldmutual.domain.entity.Ability
import com.ikiugu.oldmutual.domain.entity.Pokemon
import com.ikiugu.oldmutual.domain.entity.PokemonDetail
import com.ikiugu.oldmutual.domain.entity.PokemonStat
import com.ikiugu.oldmutual.domain.error.PokemonError
import com.ikiugu.oldmutual.domain.error.Result
import com.ikiugu.oldmutual.domain.usecase.GetPokemonDetailUseCase
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
import org.mockito.kotlin.any
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@RunWith(MockitoJUnitRunner::class)
class PokemonDetailViewModelTest : CoroutineTest() {

    @Mock
    private lateinit var getPokemonDetailUseCase: GetPokemonDetailUseCase

    private lateinit var viewModel: PokemonDetailViewModel

    override fun setup() {
        super.setup()
        whenever(getPokemonDetailUseCase(any())).thenReturn(flowOf(Result.Loading))
        viewModel = PokemonDetailViewModel(getPokemonDetailUseCase)
    }

    @Test
    fun shouldHandleLoadingState() = runTest {
        whenever(getPokemonDetailUseCase(1)).thenReturn(flowOf(Result.Loading))

        viewModel.loadPokemonDetail(1)

        val state = viewModel.uiState.value
        assertNull(state.pokemonDetail)
        assertTrue(state.isLoading)
        assertNull(state.error)
    }

    @Test
    fun shouldEmitDetailOnSuccessfulLoad() = runTest {
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

        whenever(getPokemonDetailUseCase(1)).thenReturn(flowOf(Result.Success(mockDetail)))

        viewModel.loadPokemonDetail(1)

        val state = viewModel.uiState.value
        assertNotNull(state.pokemonDetail)
        assertEquals(mockDetail, state.pokemonDetail)
        assertFalse(state.isLoading)
        assertNull(state.error)
    }

    @Test
    fun shouldEmitErrorOnLoadFailure() = runTest {
        val error = PokemonError.NetworkError
        whenever(getPokemonDetailUseCase(1)).thenReturn(flowOf(Result.Error(error)))

        viewModel.loadPokemonDetail(1)

        val state = viewModel.uiState.value
        assertNull(state.pokemonDetail)
        assertFalse(state.isLoading)
        assertEquals(error, state.error)
    }

    @Test
    fun shouldReloadDetailOnRetry() = runTest {
        val mockPokemon = Pokemon(1, "bulbasaur", "image_url", listOf("grass"), 64, 7, 69)
        val mockDetail = PokemonDetail(
            pokemon = mockPokemon,
            stats = emptyList(),
            abilities = emptyList(),
            generation = "Generation I",
            captureRate = 45
        )

        whenever(getPokemonDetailUseCase(1)).thenReturn(flowOf(Result.Success(mockDetail)))

        viewModel.loadPokemonDetail(1)
        viewModel.retry(1)

        verify(getPokemonDetailUseCase, times(2)).invoke(1)
        
        val state = viewModel.uiState.value
        assertNotNull(state.pokemonDetail)
        assertNull(state.error)
    }

    @Test
    fun shouldEmitErrorWhenPokemonNotFound() = runTest {
        val error = PokemonError.NotFoundError
        whenever(getPokemonDetailUseCase(999)).thenReturn(flowOf(Result.Error(error)))

        viewModel.loadPokemonDetail(999)

        val state = viewModel.uiState.value
        assertNull(state.pokemonDetail)
        assertFalse(state.isLoading)
        assertEquals(error, state.error)
    }
}
