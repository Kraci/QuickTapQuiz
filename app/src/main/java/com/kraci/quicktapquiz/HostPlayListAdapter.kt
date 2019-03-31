package com.kraci.quicktapquiz

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kraci.quicktapquiz.databinding.RecyclerviewItemVotedBinding

class HostPlayListAdapter : RecyclerView.Adapter<HostPlayListAdapter.VotedTeamViewHolder>() {

    var clickListener: ClickListener? = null
    var teams: List<Team> = mutableListOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    interface ClickListener {
        fun onCorrectClick(team: Team)
        fun onWrongClick(team: Team)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VotedTeamViewHolder {
        return VotedTeamViewHolder(RecyclerviewItemVotedBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int = teams.size

    override fun onBindViewHolder(holder: VotedTeamViewHolder, position: Int) {
        holder.update(teams[position])
    }

    inner class VotedTeamViewHolder(private val binding: RecyclerviewItemVotedBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.textViewCorrect.setOnClickListener { clickListener?.onCorrectClick(teams[adapterPosition]) }
            binding.textViewWrong.setOnClickListener { clickListener?.onWrongClick(teams[adapterPosition]) }
        }

        fun update(team: Team) {
            binding.textViewTeamName.text = team.teamName
        }

    }

}