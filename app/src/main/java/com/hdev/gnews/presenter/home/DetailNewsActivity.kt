package com.hdev.gnews.presenter.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.text.HtmlCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.hdev.gnews.R
import com.hdev.gnews.core.parcelable
import com.hdev.gnews.core.toTimeAgo
import com.hdev.gnews.data.source.local.room.entity.NewsEntity
import com.hdev.gnews.databinding.ActivityDetailNewsBinding
import com.hdev.gnews.domain.model.news.ArticlesItem
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DetailNewsActivity : AppCompatActivity() {
    companion object {
        const val ARTICLE = "article"
    }

    //view binding
    private lateinit var binding: ActivityDetailNewsBinding
    private val viewModel: DetailNewsViewModel by viewModels()
    private var isFavorite: Boolean = false
    private var article: ArticlesItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDetailNewsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        //get data from intent
        article = intent.parcelable<ArticlesItem>(ARTICLE)
        article?.let {
            setupUI(it)
            observeFavoriteStatus(it.url ?: "")
        }
    }

    private fun setupUI(article: ArticlesItem) {
        setupToolbar()
        with(binding) {
            // Load image from url using Glide
            Glide.with(ivFeaturedImage.context)
                .load(article.urlToImage)
                .centerCrop()
                .into(ivFeaturedImage)

            tvFeaturedTitle.text = article.title
            tvFeaturedTime.text = article.publishedAt.toTimeAgo()
            tvFeaturedDesc.text = article.description
            tvFeaturedSource.text = article.source?.name
            article.content?.let {
                tvFeaturedContent.text = HtmlCompat.fromHtml(it, HtmlCompat.FROM_HTML_MODE_LEGACY)
            }

            btnReadArticle.setOnClickListener {
                //open custom tabs intent to read full article
                val customTabsIntent = CustomTabsIntent.Builder()
                    .setToolbarColor(
                        ContextCompat.getColor(
                            this@DetailNewsActivity,
                            R.color.primary_blue
                        )
                    )
                    .setShowTitle(true)
                    .build()
                article.url?.toUri()
                    ?.let { customTabsIntent.launchUrl(this@DetailNewsActivity, it) }
            }

        }
    }

    private fun observeFavoriteStatus(url: String) {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.isFavorite(url).collectLatest {
                    isFavorite = it
                    invalidateOptionsMenu()
                }
            }
        }
    }

    @SuppressLint("PrivateResource", "UseCompatLoadingForDrawables")
    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.detail_news_toolbar_menu, menu)
        val favoriteItem = menu?.findItem(R.id.action_save)
        favoriteItem?.setIcon(
            if (isFavorite) R.drawable.baseline_bookmark_24
            else R.drawable.outline_bookmark_24
        )
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_save -> {
                article?.let {
                    val newsEntity = NewsEntity(
                        url = it.url ?: "",
                        title = it.title,
                        author = it.author,
                        description = it.description,
                        urlToImage = it.urlToImage,
                        publishedAt = it.publishedAt,
                        content = it.content,
                        sourceName = it.source?.name
                    )
                    viewModel.toggleFavorite(newsEntity, isFavorite)
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}