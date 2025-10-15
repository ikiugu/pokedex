package com.ikiugu.pokedex.domain.error

sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error<T>(val exception: PokemonError) : Result<T>()
    object Loading : Result<Nothing>()
}
