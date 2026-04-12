package com.hdev.gnews.presenter.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.hdev.gnews.R
import com.hdev.gnews.databinding.FragmentHomeBinding
import com.hdev.gnews.domain.model.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlin.getValue
import com.google.android.material.chip.Chip
import com.hdev.gnews.core.startActivity


@AndroidEntryPoint
class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    
    //view model
    private val viewModel: HomeViewModel by viewModels()

    //Adapter using lazy for performance
    private val newsAdapter: HomeNewsAdapter by lazy {
        HomeNewsAdapter { article ->
            requireContext().startActivity<DetailNewsActivity>(DetailNewsActivity.ARTICLE to article)
        }
    }

    private var currentCategory: String? = null
    private var isLoadMoreAction = false


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        setupObservers()

    }

    /**
     * setup all UI
     */
    private fun setupUI() {
        //SETUP TOOLBAR
        binding.toolbar.setNavigationOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }

        setupDrawer()
        setupChipCategory()
        setupRecyclerView()
        setupSwipeRefresh()

    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            isLoadMoreAction = false
            viewModel.getTopHeadline(category = currentCategory)
        }
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

        categories.forEachIndexed { index, categoryName ->
            val chip = layoutInflater.inflate(R.layout.chip_category, chipGroup, false) as Chip
            // Set text dan ID
            chip.text = categoryName
            chip.id = View.generateViewId()
            chip.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    isLoadMoreAction = false
                    currentCategory = categoryName
                    viewModel.getTopHeadline(category = categoryName)
                }
            }

            chipGroup.addView(chip)

            //chose Chip first for the default
            if (index == 0) {
                currentCategory = categoryName
                chip.isChecked = true
            }
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
                        viewModel.getTopHeadline(category = currentCategory, isLoadMore = true)
                    }
                }
            })
        }
    }


    private fun setupDrawer() {
        binding.navigationView.setNavigationItemSelectedListener { menuItem ->
            // Handle navigation view item clicks here
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }

    //Observe
    private fun setupObservers() {

        lifecycleScope.launch {

            //ensure that observe stop when fragment is in background
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.topHeadlineState.collect { state ->
                        when (state) {
                            is Resource.Loading -> {
                                if (isLoadMoreAction) {
                                    newsAdapter.setLoading(true)
                                } else {
                                    showLoading(true)
                                    showEmptyView(false)
                                    // Only hide RV if it's NOT a load more request
                                    if (!binding.swipeRefresh.isRefreshing && newsAdapter.itemCount == 0) {
                                        binding.rvNews.visibility = View.GONE
                                    }
                                }
                            }

                            is Resource.Success -> {
                                newsAdapter.setLoading(false)
                                showLoading(false)
                                val articles = state.data?.articles?.filterNotNull() ?: emptyList()
                                if (articles.isEmpty()) {
                                    showEmptyView(true, getString(R.string.empty_home))
                                    binding.rvNews.visibility = View.GONE
                                } else {
                                    showEmptyView(false)
                                    binding.rvNews.visibility = View.VISIBLE
                                    newsAdapter.submitList(articles)
                                }

                            }

                            is Resource.Error -> {
                                newsAdapter.setLoading(false)
                                showLoading(false)
                                if (newsAdapter.itemCount == 0) {
                                    binding.rvNews.visibility = View.GONE
                                    showEmptyView(true, state.message ?: "An error occurred")
                                }
                                showSnackBar(state.message ?: "")
                            }

                            else -> {}
                        }
                    }
                }
                launch {
                    viewModel.errorMessage.collect { message ->
                        showSnackBar(message)
                    }
                }
            }
        }
    }

    private fun showEmptyView(show: Boolean, message: String? = null) {
        binding.emptyView.root.visibility = if (show) View.VISIBLE else View.GONE
        message?.let { binding.emptyView.tvMessage.text = it }
    }

    /**
     * helper function to show the SnackBar message
     */
    private fun showSnackBar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).apply {
            setBackgroundTint(
                androidx.core.content.ContextCompat.getColor(
                    requireContext(),
                    android.R.color.holo_red_dark
                )
            )
            setTextColor(
                androidx.core.content.ContextCompat.getColor(
                    requireContext(),
                    android.R.color.white
                )
            )
            show()
        }
    }

    private fun showLoading(isShow: Boolean) {
        binding.swipeRefresh.isRefreshing = isShow
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
