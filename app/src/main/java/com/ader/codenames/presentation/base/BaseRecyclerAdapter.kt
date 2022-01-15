package com.ader.eventbudget20.presentation.base

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

abstract class BaseRecyclerAdapter<T: Any>: RecyclerView.Adapter<BaseRecyclerAdapter<T>.BaseViewHolder>() {
    protected val data = ArrayList<T>()

    var onItemClick: ((item: T) -> Unit)? = null

    var onItemPositionClick: ((position: Int) -> Unit)? = null

    fun setData(newData: List<T>){
        data.clear()
        data.addAll(newData)
        notifyDataSetChanged()
    }

    fun addNewItem(item: T){
        data.add(item)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    abstract inner class BaseViewHolder(view: View): RecyclerView.ViewHolder(view){
        private fun setClickListeners(position: Int){
            itemView.setOnClickListener {
                onItemClick?.let {
                    it(data[position])
                }

                onItemPositionClick?.let {
                    it(position)
                }
            }
        }

        open fun bind(position: Int){
            setClickListeners(position)
            bind(data[position])
        }

        abstract fun bind(item: T)
    }
}