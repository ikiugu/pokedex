package com.ikiugu.oldmutual.domain.error

sealed class PokemonError : Throwable() {
    object NetworkError : PokemonError()
    object DatabaseError : PokemonError()
    object NotFoundError : PokemonError()
    object TimeoutError : PokemonError()
    data class UnknownError(val throwable: Throwable) : PokemonError()
}

fun Throwable.toPokemonError(): PokemonError {
    return when (this) {
        is PokemonError -> this
        else -> PokemonError.UnknownError(this)
    }
}
