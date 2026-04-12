package com.hdev.gnews.presenter.trends

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar
import com.hdev.gnews.R
import com.hdev.gnews.core.startActivity
import com.hdev.gnews.databinding.FragmentTrendsBinding
import com.hdev.gnews.domain.model.Resource
import com.hdev.gnews.presenter.home.DetailNewsActivity
import com.hdev.gnews.presenter.home.HomeNewsAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TrendsFragment : Fragment() {
    private var _binding: FragmentTrendsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: EverythingViewModel by viewModels()

    private val newsAdapter: HomeNewsAdapter by lazy {
        HomeNewsAdapter(savedNews = true) { article ->
            requireContext().startActivity<DetailNewsActivity>(DetailNewsActivity.ARTICLE to article)
        }
    }

    private var currentCategory: String? = null
    private var currentQuery: String? = "news"
    private var isLoadMoreAction = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTrendsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        setupObservers()
        
        // Initial load
        viewModel.getEverything(query = currentQuery, category = currentCategory)
    }

    private fun setupUI() {
        setupSearchView()
        setupChipCategory()
        setupRecyclerView()
        setupSwipeRefresh()
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                currentQuery = query
                isLoadMoreAction = false
                viewModel.getEverything(query = currentQuery, category = currentCategory)
                binding.searchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
    }

    private fun setupChipCategory() {
        val categories = listOf(
            "business",
            "entertainment",
            "general",
            "health",
            "science",
            "sports",
            "technology"
        )
        val chipGroup = binding.chipGroupCategory
        chipGroup.removeAllViews()

        // Add "All" or similar if needed, but following HomeFragment style
        categories.forEach { categoryName ->
            val chip = layoutInflater.inflate(R.layout.chip_category, chipGroup, false) as Chip
            chip.text = categoryName
            chip.id = View.generateViewId()
            chip.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    isLoadMoreAction = false
                    currentCategory = categoryName
                    viewModel.getEverything(query = currentQuery, category = categoryName)
                }
            }
            chipGroup.addView(chip)
        }
    }

    private fun setupRecyclerView() {
        binding.rvNews.apply {
            adapter = newsAdapter
            val layoutManager = LinearLayoutManager(requireContext())
            this.layoutManager = layoutManager
            setHasFixedSize(true)

            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val visibleItemCount = layoutManager.childCount
                    val totalItemCount = layoutManager.itemCount
                    val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                        && firstVisibleItemPosition >= 0
                    ) {
                        isLoadMoreAction = true
                        viewModel.getEverything(
                            query = currentQuery,
                            category = currentCategory,
                            isLoadMore = true
                        )
                    }
                }
            })
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            isLoadMoreAction = false
            viewModel.getEverything(query = currentQuery, category = currentCategory)
        }
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.everythingState.collect { state ->
                    when (state) {
                        is Resource.Loading -> {
                            if (isLoadMoreAction) {
                                newsAdapter.setLoading(true)
                            } else {
                                showLoading(true)
                            }
                        }
                        is Resource.Success -> {
                            newsAdapter.setLoading(false)
                            showLoading(false)
                            newsAdapter.submitList(state.data?.articles)
                        }
                        is Resource.Error -> {
                            newsAdapter.setLoading(false)
                            showLoading(false)
                            showSnackBar(state.message ?: "An error occurred")
                        }
                        else -> {}
                    }
                }
            }
        }
    }

    private fun showLoading(isShow: Boolean) {
        binding.swipeRefresh.isRefreshing = isShow
    }

    private fun showSnackBar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
