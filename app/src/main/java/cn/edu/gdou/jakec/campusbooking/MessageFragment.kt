package cn.edu.gdou.jakec.campusbooking

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import cn.edu.gdou.jakec.campusbooking.adapter.MessageListAdapter
import cn.edu.gdou.jakec.campusbooking.databinding.FragmentMessageBinding

class MessageFragment : Fragment() {

    private lateinit var binding: FragmentMessageBinding
    private lateinit var viewModel: MessageViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_message, container, false)

        viewModel = ViewModelProvider(this).get(MessageViewModel::class.java)

        val adapter = MessageListAdapter()
        binding.recyclerView.adapter = adapter

        viewModel.messages.observe(viewLifecycleOwner, {
            it?.let {
//                adapter.submitList(it.sortedBy { it.day })
                adapter.submitList(it)
            }
        })

        viewModel.isRefreshing.observe(viewLifecycleOwner, {
            it?.let {
                binding.swipeRefresh.isRefreshing = it
            }
        })

        binding.appBar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.swipeRefresh.setOnRefreshListener {
            viewModel.getMessages()
        }

        return binding.root
    }

}