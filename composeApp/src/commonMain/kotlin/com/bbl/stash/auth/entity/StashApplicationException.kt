package com.bbl.stash.auth.entity

class StashApplicationException(
    val errorCode: Int,
    message: String
) : Exception(message)
