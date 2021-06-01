package cn.edu.gdou.jakec.campusbooking

import android.app.AlertDialog
import android.content.Context
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import cn.edu.gdou.jakec.campusbooking.databinding.FragmentManageStudyApplyBinding

class ManageStudyApplyFragment : Fragment() {

    private lateinit var binding: FragmentManageStudyApplyBinding

    private lateinit var viewModel: ManageStudyApplyViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_manage_study_apply,
            container,
            false
        )

        viewModel = ViewModelProvider(this).get(ManageStudyApplyViewModel::class.java)

//        viewModel.getRoom(ManageStudyRoomEditFragmentArgs.fromBundle(requireArguments()).id)

        viewModel.error.observe(viewLifecycleOwner, {
            it?.let {
                Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                binding.submitButton.isEnabled = true
            }
        })

        viewModel.finish.observe(viewLifecycleOwner, {
            it?.let {
                if (it) {
                    Toast.makeText(context,
                        getString(R.string.your_application_has_been_sent),
                        Toast.LENGTH_SHORT).show()
                    requireActivity().onBackPressed()
                }
            }
        })

        binding.appBar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.submitButton.setOnClickListener {
            if (isNotEmpty()) {
                val inputMethodManager = activity?.getSystemService(Context.INPUT_METHOD_SERVICE)
                        as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(requireView().getWindowToken(), 0)
                binding.submitButton.isEnabled = false
                viewModel.apply(binding.realname.editText?.text.toString(),
                    binding.phone.editText?.text.toString(),
                    binding.address.editText?.text.toString(),
                    binding.reason.editText?.text.toString())
            } else {
                Toast.makeText(context,
                    getString(R.string.the_contents_cant_be_null),
                    Toast.LENGTH_SHORT).show()
            }
        }


        return binding.root
    }

    private fun isNotEmpty(): Boolean {
        return (binding.realname.editText?.text.toString()
            .isNotEmpty() && binding.phone.editText?.text.toString()
            .isNotEmpty() && binding.address.editText?.text.toString()
            .isNotEmpty() && binding.reason.editText?.text.toString().isNotEmpty())
    }

}
