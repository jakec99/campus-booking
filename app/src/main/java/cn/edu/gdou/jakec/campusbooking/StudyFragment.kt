package cn.edu.gdou.jakec.campusbooking

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment.findNavController
import androidx.navigation.fragment.findNavController
import cn.edu.gdou.jakec.campusbooking.adapter.StudyRoomListAdapter
import cn.edu.gdou.jakec.campusbooking.data.Role
import cn.edu.gdou.jakec.campusbooking.data.StudyRoom
import cn.edu.gdou.jakec.campusbooking.data.StudyDatabase
import cn.edu.gdou.jakec.campusbooking.databinding.FragmentStudyBinding
import com.google.android.material.snackbar.Snackbar
import timber.log.Timber

class StudyFragment : Fragment(), StudyRoomListAdapter.StudyRoomClickListener,
    SearchView.OnQueryTextListener {

    private lateinit var viewModel: StudyViewModel

    private lateinit var binding: FragmentStudyBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_study, container, false)

        val application = requireNotNull(this.activity).application
        val dataSource = StudyDatabase.getInstance(application).studyRoomDao
        val viewModelFactory = StudyViewModelFactory(dataSource, application)

        viewModel = ViewModelProvider(
            this, viewModelFactory).get(StudyViewModel::class.java)

        binding.lifecycleOwner = this

        val adapter = StudyRoomListAdapter(this)
        binding.recyclerView.adapter = adapter

        binding.searchView.setOnQueryTextListener(this)

        viewModel.rooms.observe(viewLifecycleOwner, {
            it?.let {
                adapter.submitList(it)
            }
        })

        viewModel.role.observe(viewLifecycleOwner, {
            it?.let {
                when (it) {
                    Role.MANAGER -> binding.managerFab.visibility = View.VISIBLE
                    Role.ADMINISTRATOR -> binding.adminFab.visibility = View.VISIBLE
                    else -> {
                        binding.adminFab.visibility = View.INVISIBLE
                        binding.managerFab.visibility = View.INVISIBLE
                    }
                }
            }
        })

        viewModel.orderId.observe(viewLifecycleOwner, {
            it?.let {
                val id = it
                Snackbar.make(binding.coordinatorLayout,
                    getString(R.string.you_have_an_order_in_progress),
                    Snackbar.LENGTH_LONG)
                    .setAction(getString(R.string.check)) {
                        findNavController()
                            .navigate(StudyFragmentDirections
                                .actionStudyFragmentToStudyOrderFragment(id))
                    }
                    .show()
            }
        })

        viewModel.isRefreshing.observe(viewLifecycleOwner, {
            it?.let {
                binding.swipeRefresh.isRefreshing = it
            }
        })

        viewModel.error.observe(viewLifecycleOwner, {
            it?.let {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            }
        })

        binding.adminFab.setOnClickListener {
            findNavController().navigate(StudyFragmentDirections.actionStudyFragmentToManageFragment())
        }

        binding.managerFab.setOnClickListener {
            findNavController().navigate(StudyFragmentDirections.actionStudyFragmentToManageStudyRoomApplyFrament())
        }

        binding.swipeRefresh.setOnRefreshListener {
            viewModel.getAllRooms()
        }

        binding.searchView.setOnClickListener {
            it as SearchView
            it.isIconified = false
        }

        binding.scanButton.setOnClickListener {
            findNavController().navigate(StudyFragmentDirections.actionStudyFragmentToScanFragment())
        }

        binding.toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.profile -> {
                    findNavController().navigate(StudyFragmentDirections.actionStudyFragmentToProfileFragment())
                    true
                }
                R.id.message -> {
                    findNavController().navigate(StudyFragmentDirections.actionStudyFragmentToMessageFragment())
                    true
                }
                else -> true
            }
        }

        return binding.root
    }

    override fun onStudyRoomClicked(room: StudyRoom) {
        findNavController(this)
            .navigate(StudyFragmentDirections
                .actionStudyFragmentToStudyRoomFragment(room))
    }

    override fun onFavButtonClicked(room: StudyRoom) {
        Timber.i("the favorite button of the room %s is clicked", room.id)
        if (room.favId.isNotEmpty()) {
            Timber.i("cancel favorite")
            viewModel.cancelFavRoom(room.id, room.favId)
        } else {
            Timber.i("set favorite")
            viewModel.setFavRoom(room.id)
        }
    }

    override fun onManageButtonClicked(room: StudyRoom) {
        findNavController().navigate(StudyFragmentDirections.actionStudyFragmentToManageStudyRoomFragment(
            room))
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        if (query != null) {
            viewModel.setKey(query)
        }
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        if (newText != null) {
            Timber.i("Get text: %s", newText)
            viewModel.setKey(newText)
        }
        return true
    }

}