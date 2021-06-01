package cn.edu.gdou.jakec.campusbooking

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import cn.edu.gdou.jakec.campusbooking.databinding.FragmentManageStudyOrderBinding

class ManageStudyOrderFragment : Fragment() {

    private lateinit var binding: FragmentManageStudyOrderBinding

    private lateinit var viewModel: ManageStudyOrderViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_manage_study_order,
            container,
            false
        )

        // Get the viewModel
        viewModel = ViewModelProvider(this).get(ManageStudyOrderViewModel::class.java)

        viewModel.setOrder(ManageStudyOrderFragmentArgs.fromBundle(requireArguments()).order)

        viewModel.order.observe(viewLifecycleOwner, {
            it?.let {
                binding.roomName.text = it.roomName
                binding.seatName.text = it.seatName
                if (it.isSitting) {
                    val status = it.userName + " " + getString(R.string.is_sitting)
                    binding.status.text = status
                    val time = getString(R.string.from) + " " + it.updateTime
                    binding.time.text = time
                } else {
                    val status = it.userName + " " + getString(R.string.is_occupying)
                    binding.status.text = status
                    val time = getString(R.string.from) + " " + it.updateTime + " " +
                            getString(R.string.to) + " " + it.expireTime
                    binding.time.text = time
                }
                binding.kickButton.isEnabled = true
            }
        })

        viewModel.finished.observe(viewLifecycleOwner, {
            it?.let {
                if (it) {
                    requireActivity().onBackPressed()
                }
            }
        })

        viewModel.error.observe(viewLifecycleOwner, {
            it?.let {
                Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                binding.kickButton.isEnabled = true
            }
        })

        binding.kickButton.setOnClickListener {
            binding.kickButton.isEnabled = false
            viewModel.finish()
        }


        return binding.root
    }

}