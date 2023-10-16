package com.example.search.presentation.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.navigation.Constants.DETAIL_MODULE
import com.example.navigation.Constants.ITEM_ID_KEY
import com.example.navigation.Constants.SEARCH_VALUE_KEY
import com.example.navigation.DeepLinkFactory
import com.example.navigation.DeepLinkHandler
import com.example.search.R
import com.example.search.databinding.FragmentSearchBinding
import com.example.search.presentation.SearchUiState
import com.example.search.presentation.SearchViewModel
import com.example.search.presentation.di.SearchProvider
import kotlinx.coroutines.launch

class SearchFragment : Fragment(R.layout.fragment_search) {
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val provider = SearchProvider.create()

    private val searchAdapter = ItemAdapter(::goToDetail)

    private val searchViewModel: SearchViewModel by lazy {
        SearchViewModel(provider.useCase)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSearchBinding.bind(view)
        setupObservers()
        setupRecyclerView()
        val searchValue =
            requireActivity().intent.data?.getQueryParameter(SEARCH_VALUE_KEY).orEmpty()
        binding.toolbar.title = searchValue.toUpperCase()
        searchViewModel.searchByText(searchValue)
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                searchViewModel.searchState
                    .collect(::renderUI)
            }
        }
    }

    private fun setupRecyclerView() {
        binding.recyclerView.apply {
            adapter = searchAdapter
        }
    }

    private fun renderUI(state: SearchUiState) {
        when {
            state.isLoading -> {
            }

            state.items != null -> {
                searchAdapter.submitList(state.items)
            }
        }
    }

    private fun goToDetail(itemId: String) {
        val deepLink = DeepLinkFactory.create(DETAIL_MODULE, mapOf(ITEM_ID_KEY to itemId))
        DeepLinkHandler.openDeepLink(requireContext(), deepLink)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}