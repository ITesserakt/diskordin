package ru.tesserakt.diskordin.core.data

class RateLimitException(message: String) : Exception("Reached rate limit. $message")
