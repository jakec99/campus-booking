package cn.edu.gdou.jakec.campusbooking

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import cn.edu.gdou.jakec.campusbooking.adapter.StudyRoomListAdapter
import cn.edu.gdou.jakec.campusbooking.data.StudyRoom
import cn.edu.gdou.jakec.campusbooking.data.StudyDatabase
import cn.edu.gdou.jakec.campusbooking.databinding.FragmentManageStudyBinding

class ManageStudyFragment : Fragment(), StudyRoomListAdapter.StudyRoomClickListener {

    private lateinit var viewModel: ManageStudyViewModel

    private lateinit var binding: FragmentManageStudyBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_manage_study, container, false)

        val application = requireNotNull(this.activity).application
        val dataSource = StudyDatabase.getInstance(application).studyRoomDao
        val viewModelFactory = ManageStudyViewModelFactory(dataSource, application)

        viewModel = ViewModelProvider(
            this, viewModelFactory).get(ManageStudyViewModel::class.java)

        binding.lifecycleOwner = this

        val adapter = StudyRoomListAdapter(this)
        binding.recyclerView.adapter = adapter

        viewModel.rooms.observe(viewLifecycleOwner, {
            it?.let {
                adapter.submitList(it)
            }
        })

        viewModel.isRefreshing.observe(viewLifecycleOwner, {
            it?.let {
                binding.swipeRefresh.isRefreshing = it
            }
        })

        binding.swipeRefresh.setOnRefreshListener {
            viewModel.getAllRooms()
        }

        binding.fab.setOnClickListener {
            findNavController().navigate(ManageFragmentDirections
                .actionManageFragmentToManageStudyRoomCreateFragment())
        }

        return binding.root
    }

    override fun onStudyRoomClicked(room: StudyRoom) {
        findNavController().navigate(ManageFragmentDirections
            .actionManageFragmentToManageStudyRoomFragment(room))
    }

    override fun onFavButtonClicked(room: StudyRoom) {}

    override fun onManageButtonClicked(room: StudyRoom) {}

}