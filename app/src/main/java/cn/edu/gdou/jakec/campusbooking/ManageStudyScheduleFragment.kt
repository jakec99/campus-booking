package cn.edu.gdou.jakec.campusbooking

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import cn.edu.gdou.jakec.campusbooking.adapter.StudyScheduleListAdapter
import cn.edu.gdou.jakec.campusbooking.data.Role
import cn.edu.gdou.jakec.campusbooking.data.StudySchedule
import cn.edu.gdou.jakec.campusbooking.databinding.FragmentStudyScheduleBinding
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

class ManageStudyScheduleFragment : Fragment(),
    StudyScheduleListAdapter.StudyScheduleClickListener {

    private lateinit var binding: FragmentStudyScheduleBinding

    private lateinit var viewModel: ManageStudyScheduleViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_study_schedule, container, false)

        viewModel = ViewModelProvider(this).get(ManageStudyScheduleViewModel::class.java)

        binding.lifecycleOwner = this

        val adapter = StudyScheduleListAdapter(this)
        binding.recyclerView.adapter = adapter

        viewModel.schedules.observe(viewLifecycleOwner, {
            it?.let {
                adapter.submitList(it.sortedBy { it.day })
            }
        })

        viewModel.title.observe(viewLifecycleOwner, {
            it?.let {
                binding.appBar.title = it
            }
        })

        viewModel.error.observe(viewLifecycleOwner, {
            it?.let {
                Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
            }
        })

        viewModel.role.observe(viewLifecycleOwner, {
            it?.let {
                if (it == Role.ADMINISTRATOR || it == Role.MANAGER) {
                    binding.fab.visibility = View.VISIBLE
                }
            }
        })

        binding.appBar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.fab.setOnClickListener {
            createSchedule()
        }

        viewModel.setRoom(ManageStudyScheduleFragmentArgs.fromBundle(requireArguments()).room)
        viewModel.getSchedules()

        return binding.root
    }

    override fun onEditButtonClicked(schedule: StudySchedule) {

        AlertDialog.Builder(context)
            .setMessage(getString(R.string.close_on_this_day))
            .setNegativeButton(getString(R.string.yes)) { dialog, which ->
                schedule.openh = 0
                schedule.openm = 0
                schedule.closeh = 0
                schedule.closem = 0
                viewModel.updateSchedule(schedule)
            }
            .setPositiveButton(getString(R.string.no)) { dialog, which ->
                updateSchedule(schedule)
            }
            .show()

    }

    override fun onDeleteButtonClicked(schedule: StudySchedule) {
        viewModel.deleteSchedule(schedule)
    }

    private fun createSchedule() {
        val schedule = StudySchedule("", 20990101, 0, 0, 0, 0, true)

        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText(getString(R.string.select_date))
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .build()

        datePicker.show(childFragmentManager, "tag")

        datePicker.addOnPositiveButtonClickListener {

            schedule.day = DateTimeFormatter.ofPattern("yyyyMMdd")
                .format(Date(it).toInstant().atZone(
                    ZoneId.systemDefault())).toInt()

            AlertDialog.Builder(context)
                .setMessage(getString(R.string.close_on_this_day))
                .setNegativeButton(getString(R.string.yes)) { dialog, which ->
                    viewModel.updateSchedule(schedule)
                }
                .setPositiveButton(getString(R.string.no)) { dialog, which ->
                    updateSchedule(schedule)
                }
                .show()

        }
    }

    private fun updateSchedule(schedule: StudySchedule) {

        val openTimePicker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_12H)
            .setTitleText(getString(R.string.selece_open_time))
            .build()

        val closeTimePicker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_12H)
            .setTitleText(getString(R.string.select_close_time))
            .build()

        openTimePicker.show(childFragmentManager, "tag")

        openTimePicker.addOnPositiveButtonClickListener {
            schedule.openh = openTimePicker.hour
            schedule.openm = openTimePicker.minute
            closeTimePicker.show(childFragmentManager, "tag")
        }

        closeTimePicker.addOnPositiveButtonClickListener {
            schedule.closeh = closeTimePicker.hour
            schedule.closem = closeTimePicker.minute
            viewModel.updateSchedule(schedule)
        }

    }

}