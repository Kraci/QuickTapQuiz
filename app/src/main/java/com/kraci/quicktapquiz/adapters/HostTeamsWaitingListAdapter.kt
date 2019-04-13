package com.kraci.quicktapquiz.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kraci.quicktapquiz.R
import com.kraci.quicktapquiz.databinding.RecyclerviewItemCheckBinding
import com.kraci.quicktapquiz.viewmodels.Team

class HostTeamsWaitingListAdapter : RecyclerView.Adapter<HostTeamsWaitingListAdapter.TeamsViewHolder>() {

    var teams: List<Team> = mutableListOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TeamsViewHolder {
        return TeamsViewHolder(RecyclerviewItemCheckBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int = teams.size

    override fun onBindViewHolder(holder: TeamsViewHolder, position: Int) {
        holder.update(teams[position])
    }

    inner class TeamsViewHolder(private val binding: RecyclerviewItemCheckBinding) : RecyclerView.ViewHolder(binding.root) {

        fun update(team: Team) {
            binding.textViewTeamName.text = team.teamName
            binding.textViewChecked.setImageResource(R.drawable.ic_check_white_36dp)
            if (team.isReady) {
                binding.textViewChecked.imageAlpha = 255
            } else {
                binding.textViewChecked.imageAlpha = 0
            }
        }

    }

}