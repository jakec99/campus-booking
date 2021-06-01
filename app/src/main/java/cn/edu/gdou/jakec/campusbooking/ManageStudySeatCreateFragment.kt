package cn.edu.gdou.jakec.campusbooking

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import cn.edu.gdou.jakec.campusbooking.databinding.FragmentManageStudySeatCreateBinding

class ManageStudySeatCreateFragment : Fragment() {

    private lateinit var viewModel: ManageStudySeatCreateViewModel

    private lateinit var binding: FragmentManageStudySeatCreateBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        binding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_manage_study_seat_create, container, false)

        viewModel = ViewModelProvider(this).get(ManageStudySeatCreateViewModel::class.java)

        viewModel.roomId = ManageStudySeatCreateFragmentArgs.fromBundle(requireArguments()).roomId
        viewModel.xIndex = ManageStudySeatCreateFragmentArgs.fromBundle(requireArguments()).xIndex
        viewModel.yIndex = ManageStudySeatCreateFragmentArgs.fromBundle(requireArguments()).yIndex

        binding.button.setOnClickListener {
            binding.button.isEnabled = false
            viewModel.create(binding.outlinedTextField.editText?.text.toString())
        }

        binding.appBar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }

        viewModel.finished.observe(viewLifecycleOwner, {
            it?.let {
                if (it) requireActivity().onBackPressed()
            }
        })

        viewModel.error.observe(viewLifecycleOwner, {
            it?.let {
                Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                binding.button.isEnabled = true
            }
        })

        return binding.root
    }

}