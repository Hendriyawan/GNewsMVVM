package com.hdev.gnews.presenter.sources

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar
import com.hdev.gnews.R
import com.hdev.gnews.core.startActivity
import com.hdev.gnews.databinding.FragmentSourcesBinding
import com.hdev.gnews.domain.model.Resource
import com.hdev.gnews.domain.model.news.SourcesItem
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Locale

@AndroidEntryPoint
class SourcesFragment : Fragment() {
    private var _binding: FragmentSourcesBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SourcesViewModel by viewModels()

    private val sourcesAdapter: SourcesAdapter by lazy {
        SourcesAdapter { source ->
            requireContext().startActivity<SourceDetailActivity>(SourceDetailActivity.SOURCE to source)
        }
    }

    private var fullSourcesList = listOf<SourcesItem>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSourcesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        setupObservers()
        viewModel.getSources()
    }

    private fun setupUI() {
        setupRecyclerView()
        setupChips()
        setupSearch()
        setupSwipeRefresh()
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.getSources()
        }
    }

    private fun setupRecyclerView() {
        binding.rvSources.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = sourcesAdapter
        }
    }

    private fun setupChips() {
        val categories = listOf("All", "Business", "Entertainment", "General", "Health", "Science", "Sports", "Technology")
        val chipGroup = binding.chipGroupCategory
        chipGroup.removeAllViews()

        categories.forEach { category ->
            val chip = layoutInflater.inflate(R.layout.chip_category, chipGroup, false) as Chip
            chip.text = category
            chip.isCheckable = true
            chip.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    val filter = if (category == "All") null else category.lowercase(Locale.ROOT)
                    viewModel.getSources(filter)
                }
            }
            chipGroup.addView(chip)
            if (category == "All") chip.isChecked = true
        }
    }

    private fun setupSearch() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val query = newText.orEmpty().lowercase(Locale.ROOT)
                filterSources(query)
                return true
            }
        })
    }

    private fun filterSources(query: String) {
        val filteredList = if (query.isEmpty()) {
            fullSourcesList
        } else {
            fullSourcesList.filter { 
                it.name?.lowercase(Locale.ROOT)?.contains(query) == true || 
                it.description?.lowercase(Locale.ROOT)?.contains(query) == true 
            }
        }
        sourcesAdapter.submitList(filteredList)
        binding.emptyView.root.isVisible = filteredList.isEmpty()
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.sourcesState.collect { resource ->
                    when (resource) {
                        is Resource.Loading -> showLoading(true)
                        is Resource.Success -> {
                            showLoading(false)
                            val sources = resource.data?.sources?.filterNotNull() ?: emptyList()
                            fullSourcesList = sources
                            // Apply current search query if any
                            val query = binding.searchView.query.toString().lowercase(Locale.ROOT)
                            filterSources(query)
                        }
                        is Resource.Error -> {
                            showLoading(false)
                            showSnackBar(resource.message ?: "An error occurred")
                        }
                    }
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.swipeRefresh.isRefreshing = isLoading
    }

    private fun showSnackBar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}