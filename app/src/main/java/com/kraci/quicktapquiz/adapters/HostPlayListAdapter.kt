package com.kraci.quicktapquiz.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.kraci.quicktapquiz.R
import com.kraci.quicktapquiz.databinding.RecyclerviewItemVotedBinding
import com.kraci.quicktapquiz.viewmodels.Team

class HostPlayListAdapter(private val context: Context) : RecyclerView.Adapter<HostPlayListAdapter.VotedTeamViewHolder>() {

    var answeringIndex = 0
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
            if (adapterPosition == answeringIndex) {
                binding.textViewTeamName.background = ContextCompat.getDrawable(context,
                    R.drawable.rounded_corners_cell
                )
                binding.textViewCorrect.background = ContextCompat.getDrawable(context,
                    R.drawable.rounded_corners_cell
                )
                binding.textViewWrong.background = ContextCompat.getDrawable(context,
                    R.drawable.rounded_corners_cell
                )
                binding.textViewCorrect.isEnabled = true
                binding.textViewWrong.isEnabled = true
            } else {
                binding.textViewTeamName.background = ContextCompat.getDrawable(context,
                    R.drawable.rounded_corners_button_disabled
                )
                binding.textViewCorrect.background = ContextCompat.getDrawable(context,
                    R.drawable.rounded_corners_button_disabled
                )
                binding.textViewWrong.background = ContextCompat.getDrawable(context,
                    R.drawable.rounded_corners_button_disabled
                )
                binding.textViewCorrect.isEnabled = false
                binding.textViewWrong.isEnabled = false
            }
        }

    }

}