package com.kraci.quicktapquiz.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kraci.quicktapquiz.databinding.RecyclerviewItemBinding
import com.kraci.quicktapquiz.viewmodels.HostedGame

class JoinQuizChooseAdapter : RecyclerView.Adapter<JoinQuizChooseAdapter.JoinQuizPickerViewHolder>() {

    var clickListener: ClickListener? = null
    var hostedGames: List<HostedGame> = mutableListOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    interface ClickListener {
        fun onItemClick(hostedGame: HostedGame)
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

        fun update(hostedGame: HostedGame) {
            binding.textView.text = hostedGame.gameName
        }

    }

}