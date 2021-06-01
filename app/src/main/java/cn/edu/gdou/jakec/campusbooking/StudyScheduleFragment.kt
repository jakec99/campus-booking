package cn.edu.gdou.jakec.campusbooking

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import cn.edu.gdou.jakec.campusbooking.adapter.StudyScheduleListAdapter
import cn.edu.gdou.jakec.campusbooking.data.StudySchedule
import cn.edu.gdou.jakec.campusbooking.databinding.FragmentStudyScheduleBinding

class StudyScheduleFragment : Fragment(),
    StudyScheduleListAdapter.StudyScheduleClickListener {

    private lateinit var binding: FragmentStudyScheduleBinding

    private lateinit var viewModel: StudyScheduleViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_study_schedule, container, false)

        viewModel = ViewModelProvider(this).get(StudyScheduleViewModel::class.java)

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

        binding.appBar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }

        viewModel.setRoom(StudyScheduleFragmentArgs.fromBundle(requireArguments()).room)
        viewModel.getSchedules()

        return binding.root
    }

    override fun onEditButtonClicked(schedule: StudySchedule) {}

    override fun onDeleteButtonClicked(schedule: StudySchedule) {}
}