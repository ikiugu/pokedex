package com.ikiugu.oldmutual.presentation.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ikiugu.oldmutual.domain.error.Result
import com.ikiugu.oldmutual.domain.error.toPokemonError
import com.ikiugu.oldmutual.domain.usecase.GetPokemonDetailUseCase
import com.ikiugu.oldmutual.presentation.ui.state.PokemonDetailState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class PokemonDetailViewModel @Inject constructor(
    private val getPokemonDetailUseCase: GetPokemonDetailUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(PokemonDetailState())
    val uiState: StateFlow<PokemonDetailState> = _uiState.asStateFlow()

    fun loadPokemonDetail(id: Int) {
        Timber.d("Loading Pokémon detail - id: $id")
        viewModelScope.launch {
            getPokemonDetailUseCase(id)
                .catch { error ->
                    Timber.e(error, "Error in Pokémon detail flow")
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = error.toPokemonError()
                        )
                    }
                }
                .collect { result ->
                    when (result) {
                        is Result.Loading -> {
                            Timber.d("Pokémon detail loading...")
                            _uiState.update { 
                                it.copy(isLoading = true, error = null)
                            }
                        }
                        is Result.Success -> {
                            Timber.d("Pokémon detail loaded successfully")
                            _uiState.update {
                                it.copy(
                                    pokemonDetail = result.data,
                                    isLoading = false,
                                    error = null
                                )
                            }
                        }
                        is Result.Error -> {
                            Timber.e("Pokémon detail error: ${result.exception}")
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    error = result.exception
                                )
                            }
                        }
                    }
                }
        }
    }

    fun retry(id: Int) {
        Timber.d("Retrying Pokémon detail load")
        _uiState.update { it.copy(error = null) }
        loadPokemonDetail(id)
    }
}
