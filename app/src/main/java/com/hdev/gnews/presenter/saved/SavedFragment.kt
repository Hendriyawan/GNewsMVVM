package com.hdev.gnews.presenter.saved

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.hdev.gnews.R
import com.hdev.gnews.core.startActivity
import com.hdev.gnews.databinding.FragmentSavedBinding
import com.hdev.gnews.domain.model.news.ArticlesItem
import com.hdev.gnews.domain.model.news.Source
import com.hdev.gnews.presenter.home.DetailNewsActivity
import com.hdev.gnews.presenter.home.HomeNewsAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SavedFragment : Fragment() {
    private var _binding: FragmentSavedBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SavedViewModel by viewModels()
    private val newsAdapter: HomeNewsAdapter by lazy {
        HomeNewsAdapter(savedNews = true) { article ->
            requireContext().startActivity<DetailNewsActivity>(DetailNewsActivity.ARTICLE to article)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSavedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupObservers()
    }

    private fun setupRecyclerView() {
        binding.rvSaved.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
        }
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.favoriteNews.collectLatest { favoriteList ->
                    val isEmpty = favoriteList.isEmpty()

                    binding.emptyView.root.isVisible = isEmpty
                    binding.rvSaved.isVisible = !isEmpty
                    if (isEmpty) {
                        binding.emptyView.tvMessage.setText(R.string.empty_saved)
                    } else {

                        val articles = favoriteList.map { entity ->
                            ArticlesItem(
                                url = entity.url,
                                title = entity.title,
                                author = entity.author,
                                description = entity.description,
                                urlToImage = entity.urlToImage,
                                publishedAt = entity.publishedAt,
                                content = entity.content,
                                source = Source(name = entity.sourceName)
                            )
                        }
                        newsAdapter.submitList(articles)
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}