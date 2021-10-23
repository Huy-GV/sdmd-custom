package com.reminders.reminders

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.reminders.AppViewModel
import com.reminders.R
import com.reminders.application.MyApplication
import android.view.Menu
import android.view.MenuItem
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.reminders.data.enum.Action
import com.reminders.topics.CreateUpdateTopicDialogFragment
import com.reminders.topics.DeleteTopicDialogFragment

class ReadReminderFragment : Fragment() {
    private var topicId: Int? = null
    private var topicName: String? = null

    private val appViewModel: AppViewModel by activityViewModels {
        val database = (activity?.application as MyApplication).database
        AppViewModel.Factory(
            database.reminderDao(),
            database.topicDao()
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            topicId = it.getInt(TOPIC_ID)
            topicName = it.getString(TOPIC_NAME)
        }
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.reminder_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.delete_topic -> {
                DeleteTopicDialogFragment(
                    topicId!!,
                    appViewModel,
                    resources.getString(R.string.delete_topic_warning, topicName)
                )
                    .show(
                        parentFragmentManager,
                        DeleteTopicDialogFragment.TAG,
                    )
            }
            R.id.edit_topic -> {

                CreateUpdateTopicDialogFragment(
                    Action.UPDATE,
                    appViewModel,
                    topicId!!,
                    topicName!!
                )
                    .show(
                        parentFragmentManager,
                        CreateUpdateTopicDialogFragment.TAG
                    )
            }
        }
        return true
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_read_reminder, container, false)

        val recycler = view.findViewById<RecyclerView>(R.id.reminder_recycler)
        val reminderAdapter = ReminderAdapter(
            appViewModel,
            resources.getString(R.string.update_reminder_label)
        )

        appViewModel
            .getReminders(topicId!!)
            .observe(this.viewLifecycleOwner) {
                    reminders -> reminderAdapter.updateData(reminders)
            }

        recycler.apply {
            adapter = reminderAdapter
            layoutManager = LinearLayoutManager(this@ReadReminderFragment.context)
        }

        view.findViewById<FloatingActionButton>(R.id.create_reminder_button).setOnClickListener {
            findNavController()
                .navigate(ReadReminderFragmentDirections
                    .actionReadReminderFragmentToCreateUpdateReminderFragment(
                        topicId!!,
                        Action.CREATE,
                        null,
                        resources.getString(R.string.add_reminder_label) + topicName
                    ))
        }

        return view
    }


    companion object {
        const val TOPIC_ID = "topic_id"
        const val TOPIC_NAME = "topic_name"
    }
}