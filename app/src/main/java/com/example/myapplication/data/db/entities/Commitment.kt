package com.example.myapplication.data.db.entities


import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.*

@Entity(tableName  = "commitment_table")
data class Commitment(
    var title: String,
    var commit: String,
    var isItDone: Boolean,
    val order: Int
){
    //data class znaci kotlinu da ova klasa podatka samo sadrzi podatke
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null//znak pitanja znaci da moze biti null

    var date: String = returnDate()

    constructor(title: String, commit: String, order: Int?): this(title, commit, false, order!!)
}

fun returnDate(): String{
    val formatter = SimpleDateFormat("dd.MM.yyyy HH:mm")
    return formatter.format(Date())
}