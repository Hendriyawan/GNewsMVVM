package com.hdev.gnews.presenter.home

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hdev.gnews.core.toTimeAgo
import com.hdev.gnews.databinding.ItemLoadingBinding
import com.hdev.gnews.databinding.ItemTopHeadlineBinding
import com.hdev.gnews.databinding.ItemTopHeadlineFeaturedBinding
import com.hdev.gnews.domain.model.news.ArticlesItem

class HomeNewsAdapter(private val savedNews: Boolean = false, private val onItemClick: (ArticlesItem) -> Unit) :
    ListAdapter<ArticlesItem, RecyclerView.ViewHolder>(
        ArticleDiffCallback()
    ) {

    private var isLoadingVisible = false

    companion object {
        private const val VIEW_TYPE_FEATURED = 0
        private const val VIEW_TYPE_STANDARD = 1
        private const val VIEW_TYPE_LOADING = 2
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            isLoadingVisible && position == itemCount - 1 -> VIEW_TYPE_LOADING
            position == 0 && !savedNews -> VIEW_TYPE_FEATURED
            else -> VIEW_TYPE_STANDARD
        }
    }

    override fun getItemCount(): Int {
        return super.getItemCount() + if (isLoadingVisible) 1 else 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when(viewType){
            VIEW_TYPE_FEATURED -> {
                val binding = ItemTopHeadlineFeaturedBinding.inflate(inflater, parent, false)
                FeatureTopHeadlineViewHolder(binding)
            }
            VIEW_TYPE_LOADING -> {
                val binding = ItemLoadingBinding.inflate(inflater, parent, false)
                LoadingViewHolder(binding)
            }
            else -> {
                val binding = ItemTopHeadlineBinding.inflate(inflater, parent, false)
                StandardTopHeadlineViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is LoadingViewHolder) return
        
        val article = getItem(position)
        when (holder){
            is FeatureTopHeadlineViewHolder -> holder.bind(article)
            is StandardTopHeadlineViewHolder -> holder.bind(article)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setLoading(isLoading: Boolean) {
        if (this.isLoadingVisible != isLoading) {
            this.isLoadingVisible = isLoading
            notifyDataSetChanged()
        }
    }


    //ViewHolder for TopHeadlineFeatured
    inner class FeatureTopHeadlineViewHolder(private val binding: ItemTopHeadlineFeaturedBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(article: ArticlesItem) {
            binding.apply {
                tvFeaturedTitle.text = article.title
                tvFeaturedDesc.text = article.description
                tvFeaturedSource.text = article.source?.name ?: "--"

                //Load Image from Url with Glide
                Glide.with(ivFeaturedImage.context)
                    .load(article.urlToImage)
                    .centerCrop().into(ivFeaturedImage)
                root.setOnClickListener { onItemClick(article) }

            }
        }
    }

    //ViewHolder for TopHeadlineStandard
    inner class StandardTopHeadlineViewHolder(private val binding: ItemTopHeadlineBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(article: ArticlesItem) {
            binding.apply {
                tvTitle.text = article.title
                tvSource.text = article.source?.name ?: "--"
                tvCategoryTime.text = "NEWS - ${article.publishedAt.toTimeAgo()}"
                ////Load Image from Url with Glide
                Glide.with(ivThumbnail.context)
                    .load(article.urlToImage)
                    .centerCrop().into(ivThumbnail)

                root.setOnClickListener { onItemClick(article) }
            }
        }
    }

    inner class LoadingViewHolder(binding: ItemLoadingBinding) : RecyclerView.ViewHolder(binding.root)

    class ArticleDiffCallback : DiffUtil.ItemCallback<ArticlesItem>() {
        override fun areItemsTheSame(oldItem: ArticlesItem, newItem: ArticlesItem): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: ArticlesItem, newItem: ArticlesItem): Boolean {
            return oldItem == newItem
        }
    }
}
