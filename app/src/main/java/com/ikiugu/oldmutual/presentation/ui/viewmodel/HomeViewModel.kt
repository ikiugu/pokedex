package com.ikiugu.oldmutual.presentation.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ikiugu.oldmutual.domain.error.Result
import com.ikiugu.oldmutual.domain.error.toPokemonError
import com.ikiugu.oldmutual.domain.usecase.GetPokemonListUseCase
import com.ikiugu.oldmutual.domain.usecase.SearchPokemonUseCase
import com.ikiugu.oldmutual.presentation.ui.state.PokemonListState
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
class HomeViewModel @Inject constructor(
    private val getPokemonListUseCase: GetPokemonListUseCase,
    private val searchPokemonUseCase: SearchPokemonUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(PokemonListState())
    val uiState: StateFlow<PokemonListState> = _uiState.asStateFlow()

    init {
        Timber.d("HomeViewModel initialized")
        loadPokemonList()
    }

    private fun loadPokemonList(page: Int = 0) {
        Timber.d("Loading Pokémon list - page: $page")
        viewModelScope.launch {
            getPokemonListUseCase(page)
                .catch { error ->
                    Timber.e(error, "Error in Pokémon list flow")
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isRefreshing = false,
                            error = error.toPokemonError()
                        )
                    }
                }
                .collect { result ->
                    when (result) {
                        is Result.Loading -> {
                            Timber.d("Pokémon list loading...")
                            _uiState.update { 
                                it.copy(
                                    isLoading = page == 0,
                                    isRefreshing = page > 0,
                                    error = null
                                )
                            }
                        }
                        is Result.Success -> {
                            Timber.d("Pokémon list loaded successfully: ${result.data.size} items")
                            _uiState.update { currentState ->
                                val newList = if (page == 0) {
                                    result.data
                                } else {
                                    currentState.pokemonList + result.data
                                }
                                currentState.copy(
                                    pokemonList = newList,
                                    isLoading = false,
                                    isRefreshing = false,
                                    error = null,
                                    hasReachedEnd = result.data.isEmpty()
                                )
                            }
                        }
                        is Result.Error -> {
                            Timber.e("Pokémon list error: ${result.exception}")
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    isRefreshing = false,
                                    error = result.exception
                                )
                            }
                        }
                    }
                }
        }
    }

    fun retry() {
        Timber.d("Retrying Pokémon list load")
        _uiState.update { it.copy(error = null) }
        loadPokemonList()
    }

    fun refresh() {
        Timber.d("Refreshing Pokémon list")
        _uiState.update { it.copy(isRefreshing = true) }
        loadPokemonList()
    }

    fun onSearchQueryChanged(query: String) {
        Timber.d("Search query changed: $query")
        _uiState.update { it.copy(searchQuery = query) }
        
        if (query.isBlank()) {
            loadPokemonList()
        } else {
            searchPokemon(query)
        }
    }

    private fun searchPokemon(query: String) {
        Timber.d("Searching Pokémon: $query")
        viewModelScope.launch {
            searchPokemonUseCase(query)
                .catch { error ->
                    Timber.e(error, "Error in Pokémon search flow")
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
                            _uiState.update { 
                                it.copy(isLoading = true, error = null)
                            }
                        }
                        is Result.Success -> {
                            Timber.d("Pokémon search completed: ${result.data.size} items")
                            _uiState.update {
                                it.copy(
                                    pokemonList = result.data,
                                    isLoading = false,
                                    error = null
                                )
                            }
                        }
                        is Result.Error -> {
                            Timber.e("Pokémon search error: ${result.exception}")
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
}
