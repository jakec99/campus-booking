package cn.edu.gdou.jakec.campusbooking

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import cn.edu.gdou.jakec.campusbooking.databinding.FragmentManageStudyRoomEditBinding
import cn.edu.gdou.jakec.campusbooking.databinding.FragmentManageStudySeatEditBinding
import cn.leancloud.types.AVGeoPoint
import timber.log.Timber

class ManageStudySeatEditFragment : Fragment() {

    private lateinit var binding: FragmentManageStudySeatEditBinding

    private lateinit var viewModel: ManageStudySeatEditViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_manage_study_seat_edit,
            container,
            false
        )

        viewModel = ViewModelProvider(this).get(ManageStudySeatEditViewModel::class.java)

        viewModel.getSeat(ManageStudySeatEditFragmentArgs.fromBundle(requireArguments()).id)

        viewModel.name.observe(viewLifecycleOwner, {
            it?.let {
                binding.nameTextField.editText?.setText(it)
            }
        })

        viewModel.finished.observe(viewLifecycleOwner, {
            it?.let {
                if (it) {
                    Toast.makeText(context, getString(R.string.done), Toast.LENGTH_SHORT).show()
                    requireActivity().onBackPressed()
                }
            }
        })

        viewModel.error.observe(viewLifecycleOwner, {
            it?.let {
                Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                binding.button.isEnabled = true
                binding.deleteButton.isEnabled = true
            }
        })

        binding.appBar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.button.setOnClickListener {
            binding.button.isEnabled = false
            val name = binding.nameTextField.editText?.text.toString()
            viewModel.edit(name)
        }

        binding.deleteButton.setOnClickListener {
            binding.deleteButton.isEnabled = false
            viewModel.delete()
        }

        return binding.root
    }

}