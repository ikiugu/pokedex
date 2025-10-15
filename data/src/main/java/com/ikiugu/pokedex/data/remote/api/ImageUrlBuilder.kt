package com.ikiugu.pokedex.data.remote.api

object ImageUrlBuilder {
    fun officialArtwork(id: Int): String =
        "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/$id.png"
}


