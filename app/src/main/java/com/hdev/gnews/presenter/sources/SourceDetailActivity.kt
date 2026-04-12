package com.hdev.gnews.presenter.sources

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.hdev.gnews.R
import com.hdev.gnews.core.parcelable
import com.hdev.gnews.core.startActivity
import com.hdev.gnews.databinding.ActivitySourceDetailBinding
import com.hdev.gnews.domain.model.Resource
import com.hdev.gnews.domain.model.news.SourcesItem
import com.hdev.gnews.presenter.home.DetailNewsActivity
import com.hdev.gnews.presenter.home.HomeNewsAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SourceDetailActivity : AppCompatActivity() {

    companion object {
        const val SOURCE = "source"
    }
    private lateinit var binding: ActivitySourceDetailBinding
    private val viewModel: SourceDetailViewModel by viewModels()
    private val newsAdapter: HomeNewsAdapter by lazy {
        HomeNewsAdapter { article ->
            startActivity<DetailNewsActivity>(DetailNewsActivity.ARTICLE to article)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySourceDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //get data from intent
        val source = intent.parcelable<SourcesItem>(SOURCE)
        if (source == null) {
            finish()
            return
        }

        setupUI(source)
        setupObservers()
        source.id?.let { viewModel.getArticlesBySource(it) }
    }

    private fun setupUI(source: SourcesItem) {
        binding.apply {
            toolbar.setNavigationOnClickListener { finish() }
            tvSourceName.text = source.name
            tvSourceDescription.text = source.description

            //button visit web
            btnVisitWebsite.setOnClickListener {
                source.url?.let { url ->
                    val intent = CustomTabsIntent.Builder()
                        .setToolbarColor(
                            ContextCompat.getColor(
                                this@SourceDetailActivity,
                                R.color.primary_blue
                            )
                        )
                        .setShowTitle(true)
                        .build()
                    intent.launchUrl(this@SourceDetailActivity, url.toUri())
                }
            }

            //recycler view
            rvArticles.apply {
                layoutManager = LinearLayoutManager(this@SourceDetailActivity)
                adapter = newsAdapter
            }

            swipeRefresh.setOnRefreshListener {
                source.id?.let { viewModel.getArticlesBySource(it) }
            }
        }
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.articlesState.collect { resource ->
                    when (resource) {
                        is Resource.Loading -> showLoading(true)
                        is Resource.Success -> {
                            showLoading(false)
                            newsAdapter.submitList(resource.data?.articles)
                        }
                        is Resource.Error -> {
                            showLoading(false)
                            // Handle error
                        }
                    }
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.swipeRefresh.isRefreshing = isLoading
    }
}
