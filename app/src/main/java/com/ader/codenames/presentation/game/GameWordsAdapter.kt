package com.ader.codenames.presentation.game

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ader.codenames.databinding.*
import com.ader.codenames.presentation.model.WordUIModel

class GameWordsAdapter : RecyclerView.Adapter<GameWordsAdapter.WordViewHolder>() {

    var wordClickListener: WordClickListener? = null

    val data = ArrayList<WordUIModel>()

    fun setData(data: List<WordUIModel>) {
        this.data.clear()
        this.data.addAll(data)
        notifyItemRangeChanged(0, data.size)
    }

    fun updateWord(wordUIModel: WordUIModel, position: Int) {
        this.data.remove(data[position])
        this.data.add(position, wordUIModel)
        notifyItemChanged(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            CLOSED_WORD -> ClosedWordViewHolder(
                ItemClosedWordBinding.inflate(
                    inflater, parent, false
                )
            )

            BLUE_WORD -> BlueWordViewHolder(
                ItemBlueWordBinding.inflate(
                    inflater, parent, false
                )
            )

            RED_WORD -> RedWordViewHolder(
                ItemRedWordBinding.inflate(
                    inflater, parent, false
                )
            )

            YELLOW_WORD -> YellowWordViewHolder(
                ItemYellowWordBinding.inflate(
                    inflater, parent, false
                )
            )

            BLACK_WORD -> BlackWordViewHolder(
                ItemBlackWordBinding.inflate(
                    inflater, parent, false
                )
            )
            else -> YellowWordViewHolder(
                ItemYellowWordBinding.inflate(
                    inflater, parent, false
                )
            )
        }
    }

    override fun onBindViewHolder(holder: WordViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun getItemViewType(position: Int): Int {
        val word = data[position]
        return when (word.type) {
            WordUIModel.WordType.CLOSED -> CLOSED_WORD
            WordUIModel.WordType.RED -> RED_WORD
            WordUIModel.WordType.BLUE -> BLUE_WORD
            WordUIModel.WordType.YELLOW -> YELLOW_WORD
            WordUIModel.WordType.BLACK -> BLACK_WORD
        }
    }

    inner class ClosedWordViewHolder(private val binding: ItemClosedWordBinding) :
        WordViewHolder(binding.root) {
        override fun bind(position: Int) {
            binding.closedWordTv.text = data[position].word

            binding.root.setOnClickListener {
                wordClickListener?.onClosedWordClick(position)
            }
        }
    }

    inner class BlueWordViewHolder(private val binding: ItemBlueWordBinding) :
        WordViewHolder(binding.root) {
        override fun bind(position: Int) {
            binding.blueWordTv.text = data[position].word
        }
    }

    inner class RedWordViewHolder(private val binding: ItemRedWordBinding) :
        WordViewHolder(binding.root) {
        override fun bind(position: Int) {
            binding.redWordTv.text = data[position].word
        }
    }

    inner class YellowWordViewHolder(private val binding: ItemYellowWordBinding) :
        WordViewHolder(binding.root) {
        override fun bind(position: Int) {
            binding.yellowWordTv.text = data[position].word
        }
    }

    inner class BlackWordViewHolder(private val binding: ItemBlackWordBinding) :
        WordViewHolder(binding.root) {
        override fun bind(position: Int) {
            binding.blackWordTv.text = data[position].word
        }
    }

    abstract inner class WordViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        abstract fun bind(position: Int)
    }

    interface WordClickListener {
        fun onClosedWordClick(position: Int)
    }

    companion object {
        private const val CLOSED_WORD = 0
        private const val RED_WORD = 1
        private const val BLUE_WORD = 2
        private const val YELLOW_WORD = 3
        private const val BLACK_WORD = 4
    }
}