package com.kraci.quicktapquiz

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kraci.quicktapquiz.databinding.RecyclerviewItemCheckBinding

class ManageQuizzesListAdapter : RecyclerView.Adapter<ManageQuizzesListAdapter.QuizViewHolder>() {

    var clickListener: ManageQuizzesListAdapter.ClickListener? = null
    var quizzes: List<Quiz> = mutableListOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    interface ClickListener {
        fun onDeleteItemClick(quiz: Quiz)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuizViewHolder {
        return QuizViewHolder(RecyclerviewItemCheckBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int = quizzes.size

    override fun onBindViewHolder(holder: QuizViewHolder, position: Int) {
        holder.update(quizzes[position])
    }

    inner class QuizViewHolder(private val binding: RecyclerviewItemCheckBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.textViewChecked.setOnClickListener { clickListener?.onDeleteItemClick(quizzes[adapterPosition]) }
        }

        fun update(quiz: Quiz) {
            binding.textViewTeamName.text = quiz.name
            binding.textViewChecked.text = "D"
        }

    }

}