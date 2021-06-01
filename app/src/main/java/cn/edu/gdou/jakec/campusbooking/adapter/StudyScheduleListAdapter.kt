package cn.edu.gdou.jakec.campusbooking.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.edu.gdou.jakec.campusbooking.MyApplication.Companion.context
import cn.edu.gdou.jakec.campusbooking.R
import cn.edu.gdou.jakec.campusbooking.data.StudySchedule
import cn.edu.gdou.jakec.campusbooking.databinding.ListItemStudyScheduleBinding
import cn.leancloud.AVException
import java.time.format.DateTimeFormatter
import java.util.*

class StudyScheduleListAdapter internal constructor(
    private val listener: StudyScheduleClickListener,
) : ListAdapter<StudySchedule, StudyScheduleListAdapter.ViewHolder>(StudyScheduleDiffCallback()) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, listener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    class ViewHolder(val binding: ListItemStudyScheduleBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: StudySchedule, listener: StudyScheduleClickListener) {

            binding.schedule = item
            binding.executePendingBindings()

            binding.editButton.visibility = View.GONE
            binding.deleteButton.visibility = View.GONE

            binding.day.text = when (item.day) {
                1 -> context.getString(R.string.monday)
                2 -> context.getString(R.string.tuesday)
                3 -> context.getString(R.string.wednesday)
                4 -> context.getString(R.string.thursday)
                5 -> context.getString(R.string.friday)
                6 -> context.getString(R.string.saturday)
                7 -> context.getString(R.string.sunday)
                else -> {
                    convertDayText(item.day)
                }
            }

            binding.time.text = convertTimeText(item)

            binding.editButton.visibility = when (item.isManageable) {
                true -> View.VISIBLE
                false -> View.GONE
            }
            binding.deleteButton.visibility = when (item.day) {
                1, 2, 3, 4, 5, 6, 7 -> View.GONE
                else -> when (item.isManageable) {
                    true -> View.VISIBLE
                    false -> View.GONE
                }
            }

            binding.editButton.setOnClickListener() {
                binding.schedule?.let {
                    listener.onEditButtonClicked(it)
                }
            }

            binding.deleteButton.setOnClickListener() {
                binding.schedule?.let {
                    listener.onDeleteButtonClicked(it)
                }
            }

        }

        private fun convertDayText(date: Int): String {
            val year = date / 10000
            val month = (date - (year * 10000)) / 100
            val day = date - (year * 10000) - (month * 100)
            val str = year.toString() + "." + month.toString() + "." + day.toString()
            return str
        }

        private fun convertTimeText(item: StudySchedule): String {
            if (
                (item.openh > item.closeh) ||
                ((item.openh == item.closeh) && (item.openm >= item.closem))
            ) {
                return context.getString(R.string.close_allcaps)
            }

            return String.format("%02d:%02d - %02d:%02d",
                item.openh,
                item.openm,
                item.closeh,
                item.closem)
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemStudyScheduleBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }

    interface StudyScheduleClickListener {
        fun onEditButtonClicked(schedule: StudySchedule)
        fun onDeleteButtonClicked(schedule: StudySchedule)
    }
}


class StudyScheduleDiffCallback : DiffUtil.ItemCallback<StudySchedule>() {
    override fun areItemsTheSame(oldItem: StudySchedule, newItem: StudySchedule): Boolean {
        return false
    }

    override fun areContentsTheSame(oldItem: StudySchedule, newItem: StudySchedule): Boolean {
        return false
    }
}