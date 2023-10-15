package com.example.search.domain.usecase

import com.example.search.domain.model.Item
import com.example.search.domain.repository.SearchRepository

class SearchUseCase(private val repository: SearchRepository) {
    suspend fun invoke(value: String) : List<Item> =
        repository.getItemsByText(value)
}