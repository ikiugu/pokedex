package com.ikiugu.pokedex.presentation.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.ikiugu.pokedex.domain.entity.Pokemon
import com.ikiugu.pokedex.domain.error.Result
import com.ikiugu.pokedex.domain.error.toPokemonError
import com.ikiugu.pokedex.domain.usecase.GetPokemonListUseCase
import com.ikiugu.pokedex.domain.usecase.SearchPokemonUseCase
import com.ikiugu.pokedex.presentation.ui.state.PokemonListState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
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
    
    val pokemonPagingFlow = getPokemonListUseCase()
        .cachedIn(viewModelScope)

    init {
        Timber.d("HomeViewModel initialized")
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        
        if (query.isBlank()) {
            _uiState.update { it.copy(searchResults = emptyList()) }
        } else {
            viewModelScope.launch {
                searchPokemonUseCase(query)
                    .catch { error ->
                        Timber.e(error, "Error in search flow")
                        _uiState.update { it.copy(error = error.toPokemonError()) }
                    }
                    .collect { result ->
                        when (result) {
                            is Result.Loading -> _uiState.update { it.copy(isLoading = true, error = null) }
                            is Result.Success -> _uiState.update { it.copy(searchResults = result.data, isLoading = false, error = null) }
                            is Result.Error -> _uiState.update { it.copy(error = result.exception, isLoading = false) }
                        }
                    }
            }
        }
    }

    fun retry() {
        Timber.d("Retrying search")
        _uiState.update { it.copy(error = null) }
        val query = _uiState.value.searchQuery
        if (query.isNotBlank()) {
            onSearchQueryChanged(query)
        }
    }
}