package com.hdev.gnews.presenter.sources

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.hdev.gnews.databinding.ItemSourceBinding
import com.hdev.gnews.domain.model.news.SourcesItem
import java.util.Locale

class SourcesAdapter(private val onItemClick: (SourcesItem) -> Unit) :
    ListAdapter<SourcesItem, SourcesAdapter.SourceViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SourceViewHolder {
        val binding = ItemSourceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SourceViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SourceViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class SourceViewHolder(private val binding: ItemSourceBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: SourcesItem) {
            binding.apply {
                tvSourceName.text = item.name
                tvSourceDescription.text = item.description
                tvInitial.text = item.name?.firstOrNull()?.toString()?.uppercase(Locale.ROOT)
                chipCategory.text = item.category?.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }
                chipCountry.text = item.country?.uppercase(Locale.ROOT)

                root.setOnClickListener { onItemClick(item) }
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<SourcesItem>() {
        override fun areItemsTheSame(oldItem: SourcesItem, newItem: SourcesItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: SourcesItem, newItem: SourcesItem): Boolean {
            return oldItem == newItem
        }
    }
}
