package com.ifs21028.lostandfound.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ifs21028.lostandfound.R
import com.ifs21028.lostandfound.data.remote.response.LostFoundsItemResponse
import com.ifs21028.lostandfound.databinding.ItemRowLafBinding

class LafAdapter :
    ListAdapter<LostFoundsItemResponse,
            LafAdapter.MyViewHolder>(DIFF_CALLBACK) {
    private lateinit var onItemClickCallback: OnItemClickCallback
    private var originalData = mutableListOf<LostFoundsItemResponse>()
    private var filteredData = mutableListOf<LostFoundsItemResponse>()
    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemRowLafBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return MyViewHolder(binding)
    }
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val data = originalData[originalData.indexOf(getItem(position))]
        holder.binding.cbItemTodoIsFinished.setOnCheckedChangeListener(null)
        holder.binding.cbItemTodoIsFinished.setOnLongClickListener(null)
        holder.bind(data)
        holder.binding.cbItemTodoIsFinished.setOnCheckedChangeListener { _, isChecked ->
            data.isCompleted = if (isChecked) 1 else 0
            holder.bind(data)
            onItemClickCallback.onCheckedChangeListener(data, isChecked)
        }
        holder.binding.btnItemTodoDetail.setOnClickListener {
            onItemClickCallback.onClickDetailListener(data.id)
        }
    }
    class MyViewHolder(val binding: ItemRowLafBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: LostFoundsItemResponse) {
            binding.apply {
                tvItemTodoTitle.text = data.title
                cbItemTodoIsFinished.isChecked = data.isCompleted == 1
                tvItemLaf.text = data.status

                if(data.cover != null) {
                    ivLaf.visibility = View.VISIBLE

                    Glide.with(itemView.context)
                        .load(data.cover)
                        .placeholder(R.drawable.ic_image_24)
                        .into(ivLaf)
                } else {
                    ivLaf.visibility = View.GONE
                }
            }
        }
    }
    fun submitOriginalList(list: List<LostFoundsItemResponse>) {
        originalData = list.toMutableList()
        filteredData = list.toMutableList()
        submitList(originalData)
    }
    fun filter(query: String) {
        filteredData = if (query.isEmpty()) {
            originalData
        } else {
            originalData.filter {
                (it.title.contains(query, ignoreCase = true))
            }.toMutableList()
        }
        submitList(filteredData)
    }
    interface OnItemClickCallback {
        fun onCheckedChangeListener(todo: LostFoundsItemResponse, isChecked: Boolean)
        fun onClickDetailListener(todoId: Int)
    }
    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<LostFoundsItemResponse>() {
            override fun areItemsTheSame(
                oldItem: LostFoundsItemResponse,
                newItem: LostFoundsItemResponse
            ): Boolean {
                return oldItem.id == newItem.id
            }
            override fun areContentsTheSame(
                oldItem: LostFoundsItemResponse,
                newItem: LostFoundsItemResponse
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}