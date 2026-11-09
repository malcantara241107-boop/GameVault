package com.example.gamevault.presentation.navigation

object GameVaultRoutes {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val HOME = "home"
    const val CREATE_GAME = "create_game"
    const val GAME_DETAIL = "game_detail/{gameId}"
    const val EDIT_GAME = "edit_game/{gameId}"

    fun gameDetail(gameId: Int): String = "game_detail/$gameId"
    fun editGame(gameId: Int): String = "edit_game/$gameId"
}
