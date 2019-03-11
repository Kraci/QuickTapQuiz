package com.kraci.quicktapquiz

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kraci.quicktapquiz.databinding.RecyclerviewItemBinding

class JoinQuizChooseAdapter : RecyclerView.Adapter<JoinQuizChooseAdapter.JoinQuizPickerViewHolder>() {

    var clickListener: ClickListener? = null
    var hostedGames: List<Game> = mutableListOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    interface ClickListener {
        fun onItemClick(hostedGame: Game)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JoinQuizPickerViewHolder {
        return JoinQuizPickerViewHolder(RecyclerviewItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount() = hostedGames.count()

    override fun onBindViewHolder(holder: JoinQuizPickerViewHolder, position: Int) {
        holder.update(hostedGames[position])
    }


    inner class JoinQuizPickerViewHolder(private val binding: RecyclerviewItemBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener { clickListener?.onItemClick(hostedGames[adapterPosition]) }
        }

        fun update(hostedGame: Game) {
            binding.textView.text = hostedGame.gameName
        }

    }

}