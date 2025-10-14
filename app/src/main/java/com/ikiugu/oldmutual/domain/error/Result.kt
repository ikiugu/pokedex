package com.ikiugu.oldmutual.domain.error

sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val exception: PokemonError) : Result<Error>()
    object Loading : Result<Nothing>()
}
