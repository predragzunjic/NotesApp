package com.example.myapplication.data

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.DiffCallback
import com.example.myapplication.data.db.entities.Commitment
import com.example.myapplication.databinding.CommitmentBinding
import java.util.*
import kotlin.collections.ArrayList

class CommitmentAdapter(
    //listener is a lambda function that we use to set a long click listener for every item of recycler view
    var commitments: ArrayList<Commitment>,
    var whichAdapter: Int,
    val listenerFinish: (Commitment) -> Boolean,

): RecyclerView.Adapter<CommitmentAdapter.CommitmentViewHolder>() {
    private val diffCallback = DiffCallback(commitments, ArrayList())

    //for every item that can fit currently on screen, this is called
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommitmentViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = CommitmentBinding.inflate(layoutInflater, parent, false)
        return CommitmentViewHolder(binding)
    }

    //here, we give values to items in recyclerview
    override fun onBindViewHolder(holder: CommitmentViewHolder, position: Int) {
        val curCommitment = commitments[position]
        holder.bind(curCommitment)
    }

    override fun getItemCount(): Int{
        return commitments.size
    }

    fun submitList(updatedList: List<Commitment>){
        diffCallback.newList = updatedList

        val diffResult = DiffUtil.calculateDiff(diffCallback)

        commitments.clear()

        commitments.addAll(updatedList)
        diffResult.dispatchUpdatesTo(this)
    }

    fun itemMoved(from: Int, to: Int) {
        Collections.swap(commitments, from, to)
        notifyItemMoved(from, to)
    }

    fun deleteItem(i: Int): Commitment{
        val delCommitment = commitments[i]

        commitments.removeAt(i)
        notifyItemRemoved(i)
        notifyItemRangeChanged (i, itemCount)

        return delCommitment
    }

    fun updateList(list: ArrayList<Commitment>){
        commitments = list
    }

    inner class CommitmentViewHolder(private val binding: CommitmentBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(commitment: Commitment){
            binding.tvTitle.text = commitment.title
            binding.tvCommit.text = commitment.commit
            binding.tvDate.text = commitment.date

            //MainActivity needs a longclicklistener, so we set it if we are called from MainActivity
            if(whichAdapter == 0){
                binding.root.setOnClickListener{
                    listenerFinish.invoke(commitment)
                }
            }

        }
    }


}