package com.example.myapplication

import androidx.recyclerview.widget.DiffUtil
import com.example.myapplication.data.db.entities.Commitment

class DiffCallback(
    val oldList: List<Commitment>,
    var newList: List<Commitment>
): DiffUtil.Callback(){

    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]
        return compareContents(oldItem, newItem)
    }

    private fun compareContents(oldItem: Commitment, newItem: Commitment): Boolean {
        if(oldItem.id == newItem.id && oldItem.title == newItem.title && oldItem.commit == newItem.commit)
            return true

        return false
    }

}