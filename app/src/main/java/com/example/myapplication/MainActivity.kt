package com.example.myapplication

import android.app.*
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.data.CommitmentAdapter
import com.example.myapplication.data.db.CommitmentDatabase
import com.example.myapplication.data.db.entities.Commitment
import com.example.myapplication.data.repositories.CommitmentRepository
import com.example.myapplication.data.viewmodels.CommitmentViewModel
import com.example.myapplication.data.viewmodels.CommitmentViewModelFactory
import com.example.myapplication.databinding.ActivityMainBinding
import com.example.myapplication.databinding.DialogAddCommitmentBinding
import java.util.*


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: CommitmentViewModel
    private var edDialog: AlertDialog? = null
    private lateinit var adapter: CommitmentAdapter
    private lateinit var preference: SharedPreferences
    private var order: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layoutInflater = LayoutInflater.from(this)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val edBinding = DialogAddCommitmentBinding.inflate(layoutInflater)

        //creating database and viewmodel
        val database = CommitmentDatabase(this)
        val repository = CommitmentRepository(database)
        val factory = CommitmentViewModelFactory(repository)

        val viewModel: CommitmentViewModel by viewModels{factory}

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        this.viewModel = viewModel
        binding.rvCommits.isLongClickable = true

        preference = getSharedPreferences(resources.getString(R.string.app_name), Context.MODE_PRIVATE)
        order = preference.getInt("ORDER", 0)
        val editor = preference.edit()

        //get dimension of the system navbar and assign padding programatically
        calcNavBar()

        //creating adapter for recycler view and passing it lambda function for
        //marking commitments as finished
        adapter = getCommitmentsAdapter(0)

        viewModel.getCommitments(0)
        viewModel.getCommitments(1)

        binding.rvCommits.adapter = adapter
        binding.rvCommits.layoutManager = LinearLayoutManager(this)

        val observer = Observer<List<Commitment>>{ unfinishedCommitments ->
            adapter.submitList(unfinishedCommitments)
        }

        viewModel.getCommitmentsLV().observe(this, observer)

        //whenever getAllCommitments() returns something new, observer is called and livedata updated
        val swipeHelperCallback = object: SwipeHelperCallback(){
            var dragFrom = -1
            var dragTo = -1

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder

            ): Boolean {
                val fromPosition = viewHolder.adapterPosition
                val toPosition = target.adapterPosition

                dragFrom = fromPosition
                dragTo = toPosition

                adapter.itemMoved(fromPosition, toPosition)

                return true
            }

            override fun clearView(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ) {
                if(dragFrom == -1 || dragTo == -1)
                    return

                val id1 = adapter.commitments[dragFrom].id
                val order1 = adapter.commitments[dragFrom].order
                val id2 = adapter.commitments[dragTo].id
                val order2 = adapter.commitments[dragTo].order

                viewModel.updateOrder(id1, order2)
                viewModel.updateOrder(id2, order1)

                dragFrom = -1
                dragTo = -1
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                deleteDialog(adapter, viewHolder)
            }
        }

        val swipeHelper = ItemTouchHelper(swipeHelperCallback)
        swipeHelper.attachToRecyclerView(binding.rvCommits)


        //save button from dialog that pops up when you want to add a commitment
        edBinding.btnAdd.setOnClickListener{
            val title = edBinding.etTitle.text.toString()
            val description = edBinding.etDescription.text.toString()
            if(title.isEmpty() || description.isEmpty())
                Toast.makeText(this@MainActivity, "Please enter correct values", Toast.LENGTH_SHORT).show()
            else{
                val commit = Commitment(title, description, order)
                order += 1
                viewModel.insert(commit)
                editor.putInt("ORDER", order)
                editor.apply()

                edBinding.etTitle.setText("")
                edBinding.etDescription.setText("")

                edDialog?.dismiss()
            }
        }

        //dismiss the dialog for adding a commitment
        edBinding.btnCancel.setOnClickListener {
            edDialog?.dismiss()
        }

        //a button that is responsible for opening a dialog for entering a commitment
        binding.fab.setOnClickListener{
            if(edDialog == null){
                edDialog = AlertDialog.Builder(this)
                    .setTitle("Enter a new commitment")
                    .setView(edBinding.root)
                    .show()
            }
            edDialog?.show()

            edBinding.etTitle.requestFocus()
        }

        //button toggle
        binding.btnCommits.setOnClickListener {
            adapter.whichAdapter = 0
            //whenever getAllCommitments() returns something new, observer is called and livedata updated
            viewModel.getFinishedCommitmentsLV().removeObserver(observer)
            viewModel.getCommitmentsLV().observe(this, observer)
        }

        binding.btnFinCommits.setOnClickListener {
            adapter.whichAdapter = 1
            viewModel.getCommitmentsLV().removeObserver(observer)
            viewModel.getFinishedCommitmentsLV().observe(this, observer)
        }
    }

    override fun onPause() {
        super.onPause()
        schedulePeriodicNotifications()
    }

    //before an activity is rendered, i get this default menu to fill it up
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.app_bar_menu, menu)
        return true
    }

    //menu options
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //if you try to delete all commitments, a dialog pops up to check are you sure
        if(item.itemId == R.id.mi_delete_all){
            if(binding.btnsToggle.checkedButtonId == binding.btnCommits.id && adapter.itemCount > 0){
                val alertBuilder = AlertDialog.Builder(this)
                    .setTitle("Do you want to delete all commitments?")
                    .setNegativeButton("No"){ _: DialogInterface, _: Int ->
                    }
                    .setPositiveButton("Yes"){ _: DialogInterface, _: Int ->
                        viewModel.deleteCommitments(adapter.whichAdapter)
                        Toast.makeText(this@MainActivity, "Deleted everything", Toast.LENGTH_SHORT).show()
                    }

                val alertDialog = alertBuilder.create()

                alertDialog.window?.attributes?.windowAnimations = R.style.DialogAnimation
                alertDialog.show()
            }
            else if(binding.btnsToggle.checkedButtonId == binding.btnCommits.id && adapter.itemCount > 0){
                Toast.makeText(this, "There is no commitments to delete", Toast.LENGTH_SHORT).show()
            }
            else if(binding.btnsToggle.checkedButtonId == binding.btnFinCommits.id && adapter.itemCount > 0){
                val alertBuilder = AlertDialog.Builder(this)
                    .setTitle("Do you want to delete all finished commitments?")
                    .setNegativeButton("No"){ _: DialogInterface, _: Int ->
                    }
                    .setPositiveButton("Yes"){ _: DialogInterface, _: Int ->

                        viewModel.deleteCommitments(adapter.whichAdapter)
                        Toast.makeText(this@MainActivity, "Deleted everything", Toast.LENGTH_SHORT).show()
                    }

                val alertDialog = alertBuilder.create()

                alertDialog.window?.attributes?.windowAnimations = R.style.DialogAnimation
                alertDialog.show()
            }
            else{
                Toast.makeText(this, "There is no finished commitments to delete", Toast.LENGTH_SHORT).show()
            }

        }

        return true
    }

    private fun getCommitmentsAdapter(whichAdapter: Int): CommitmentAdapter{
        //creating adapter for recycler view and passing it lambda function for marking commitments as finished
        return CommitmentAdapter(arrayListOf(),  whichAdapter){ commitment ->
            val alertBuilder = AlertDialog.Builder(this)
                .setTitle("You've done your commitment?")
                .setNegativeButton("No") {_: DialogInterface, _: Int ->
                }
                .setPositiveButton("Yes") { _: DialogInterface, _: Int ->
                    viewModel.update(commitment.id)
                    Toast.makeText(this@MainActivity, "Commitment finished", Toast.LENGTH_SHORT).show()
                }

            val alertDialog = alertBuilder.create()

            alertDialog.window?.attributes?.windowAnimations = R.style.DialogAnimation
            alertDialog.show()

            true
        }
    }

    private fun deleteDialog(adapter: CommitmentAdapter, viewHolder: RecyclerView.ViewHolder){
        val alertBuilder = AlertDialog.Builder(this)
            .setTitle("Do you want to delete this commitment?")
            .setNegativeButton("No") {_: DialogInterface, _: Int ->
                adapter.notifyItemChanged(viewHolder.adapterPosition)
            }
            .setPositiveButton("Yes") { _: DialogInterface, _: Int ->
                viewModel.delete(adapter.deleteItem(viewHolder.adapterPosition))
                Toast.makeText(this@MainActivity, "Commitment deleted", Toast.LENGTH_SHORT).show()
            }

        val alertDialog = alertBuilder.create()

        alertDialog.window?.attributes?.windowAnimations = R.style.DialogAnimation
        alertDialog.show()
    }

    //we use AlarmManager to implement notifications that can run while app is closed
    //Notification inherits broadcast receiver and calls necessary methods from NotificationHelper
    private fun schedulePeriodicNotifications() {
        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, Notification::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this, 1,
            intent, 0)

        val currentDate = Date()

        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, currentDate.time
            , AlarmManager.INTERVAL_DAY, pendingIntent)
    }

    private fun calcNavBar(){
        val resources = this.resources

        val resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android")
        var dimension = 0
        if(resourceId > 0)
            dimension =  resources.getDimensionPixelSize(resourceId)

        binding.rvCommits.setPadding(0,16,0,dimension)
    }
}