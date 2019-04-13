package com.kraci.quicktapquiz.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kraci.quicktapquiz.databinding.RecyclerviewItemBinding
import com.kraci.quicktapquiz.databinding.RecyclerviewItemQuestionBinding
import com.kraci.quicktapquiz.viewmodels.GameAdapter
import com.kraci.quicktapquiz.viewmodels.GameAdapterType

class HostPlayQuestionsListAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var items: List<GameAdapter> = mutableListOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var clickListener: ClickListener? = null

    interface ClickListener {
        fun onQuestionClick(question: GameAdapter, position: Int)
    }

    override fun getItemViewType(position: Int): Int {
        return items[position].type.ordinal
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            GameAdapterType.CATEGORY.ordinal -> return CategoryViewHolder(RecyclerviewItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            GameAdapterType.QUESTION.ordinal -> return QuestionViewHolder(RecyclerviewItemQuestionBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        }
        assert(false) {
            println("FATAL ERROR")
        }
        return CategoryViewHolder(RecyclerviewItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is CategoryViewHolder -> holder.update(items[position])
            is QuestionViewHolder -> holder.update(items[position])
        }
    }

    inner class CategoryViewHolder(private val binding: RecyclerviewItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun update(category: GameAdapter) {
            binding.textView.text = category.text
        }

    }

    inner class QuestionViewHolder(private val binding: RecyclerviewItemQuestionBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener { clickListener?.onQuestionClick(items[adapterPosition], adapterPosition) }
        }

        fun update(question: GameAdapter) {
            binding.textViewQuestion.text = question.value.toString()
        }

    }

}