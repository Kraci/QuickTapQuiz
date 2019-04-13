package com.kraci.quicktapquiz.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kraci.quicktapquiz.database.Quiz
import com.kraci.quicktapquiz.databinding.RecyclerviewItemBinding

class HostQuizPickerListAdapter : RecyclerView.Adapter<HostQuizPickerListAdapter.QuizPickerViewHolder>() {

    var clickListener: ClickListener? = null
    var quizzes: List<Quiz> = mutableListOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    interface ClickListener {
        fun onItemClick(quiz: Quiz)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuizPickerViewHolder {
        return QuizPickerViewHolder(RecyclerviewItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: QuizPickerViewHolder, position: Int) {
        holder.update(quizzes[position])
    }

    override fun getItemCount() = quizzes.size


    inner class QuizPickerViewHolder(private val binding: RecyclerviewItemBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener { clickListener?.onItemClick(quizzes[adapterPosition]) }
        }

        fun update(quiz: Quiz) {
            binding.textView.text = quiz.name
        }

    }

}