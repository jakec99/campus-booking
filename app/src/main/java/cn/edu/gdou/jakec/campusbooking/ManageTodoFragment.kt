package cn.edu.gdou.jakec.campusbooking

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import cn.edu.gdou.jakec.campusbooking.adapter.ManageTodoListAdapter
import cn.edu.gdou.jakec.campusbooking.data.ManageTodo
import cn.edu.gdou.jakec.campusbooking.data.Todo
import cn.edu.gdou.jakec.campusbooking.databinding.FragmentManageTodoBinding

class ManageTodoFragment : Fragment(), ManageTodoListAdapter.ManageTodoClickListener {

    private lateinit var binding: FragmentManageTodoBinding

    private lateinit var viewModel: ManageTodoViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_manage_todo,
            container,
            false
        )

        viewModel = ViewModelProvider(this).get(ManageTodoViewModel::class.java)

        val adapter = ManageTodoListAdapter(this)
        binding.recyclerView.adapter = adapter

        viewModel.todos.observe(viewLifecycleOwner, {
            adapter.submitList(it)
        })

        viewModel.isRefreshing.observe(viewLifecycleOwner, {
            it?.let {
                binding.swipeRefresh.isRefreshing = it
            }
        })

        binding.swipeRefresh.setOnRefreshListener {
            viewModel.getTodos()
        }

        return binding.root
    }

    override fun onManagerTodoClicked(item: ManageTodo) {

        when (item.type) {
            Todo.MANAGER_APPLICATION -> findNavController().navigate(ManageFragmentDirections.actionManageFragmentToManageStudyCheckFragment(
                item))
            Todo.STUDY_ROOM_APPLICATION -> findNavController().navigate(ManageFragmentDirections.actionManageFragmentToManageStudyRoomCheckFragment(
                item))
        }

    }

}