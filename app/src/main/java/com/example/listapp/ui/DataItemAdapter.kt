package com.example.listapp.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.listapp.R
import com.example.listapp.models.DataResponseItem
import kotlinx.android.synthetic.main.data_item.view.*


class DataItemAdapter(var clickDownload: ClickDownload) : RecyclerView.Adapter<DataItemAdapter.DataItemViewHolder>(){

    inner class DataItemViewHolder(itemView: View):RecyclerView.ViewHolder(itemView)

    private val diffCallback = object : DiffUtil.ItemCallback<DataResponseItem>(){
        override fun areItemsTheSame(oldItem: DataResponseItem, newItem: DataResponseItem): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: DataResponseItem, newItem: DataResponseItem): Boolean {
            return oldItem == newItem
        }
    }

    private val differ = AsyncListDiffer(this,diffCallback)

    fun submitList(list: List<DataResponseItem>) = differ.submitList(list)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataItemViewHolder {
        return DataItemViewHolder(
            LayoutInflater.from(
                parent.context
            ).inflate(
                R.layout.data_item,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder:DataItemViewHolder, position: Int) {

        val item = differ.currentList[position]

        holder.itemView.apply {
            tvName.text = "${item.name}"
            tvType.text = "${item.type}"

        }
        holder.itemView.setOnClickListener {
            clickDownload.download(item.url!!)
            holder.itemView.apply {
                img_download.visibility = View.GONE
                img_completedDownload.visibility = View.VISIBLE
            }
        }

    }
}