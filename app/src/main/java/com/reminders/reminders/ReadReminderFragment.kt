package com.reminders.reminders

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.reminders.AppViewModel
import com.reminders.R
import com.reminders.application.MyApplication

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class ReadReminderFragment : Fragment() {
    private var topicId: Int? = null
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
        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_read_reminder, container, false)

        val recycler = view.findViewById<RecyclerView>(R.id.reminder_recycler)
        val reminderAdapter = ReminderAdapter(appViewModel)

        appViewModel
            .getReminders(topicId!!)
            .observe(this.viewLifecycleOwner) {
                    reminders -> reminderAdapter.updateData(reminders)
            }

        recycler.apply {
            adapter = reminderAdapter
            layoutManager = LinearLayoutManager(this@ReadReminderFragment.context)
        }

        view.findViewById<Button>(R.id.create_reminder_button).setOnClickListener {
            val action =
                ReadReminderFragmentDirections
                    .actionReadReminderFragmentToCreateReminderFragment(topicId!!)
            findNavController().navigate(action)
        }

        return view
    }

    companion object {
        val TOPIC_ID = "topic_id"
    }
}