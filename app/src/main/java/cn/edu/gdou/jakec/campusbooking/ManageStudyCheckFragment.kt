package cn.edu.gdou.jakec.campusbooking

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import cn.edu.gdou.jakec.campusbooking.databinding.FragmentManageStudyCheckBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

class ManageStudyCheckFragment : Fragment() {

    private lateinit var binding: FragmentManageStudyCheckBinding
    private lateinit var viewModel: ManageStudyCheckViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_manage_study_check,
            container,
            false
        )
        viewModel = ViewModelProvider(this).get(ManageStudyCheckViewModel::class.java)
        viewModel.setTodo(ManageStudyCheckFragmentArgs.fromBundle(requireArguments()).todo)

        viewModel.imgUrl.observe(viewLifecycleOwner, {
            it?.let {
                Glide.with(binding.image.context)
                    .load(it)
                    .apply(RequestOptions()
                        .placeholder(R.drawable.loading_animation)
                        .error(R.drawable.ic_broken_image))
                    .into(binding.image)
            }
        })

        viewModel.nickname.observe(viewLifecycleOwner, {
            it?.let {
                binding.nickname.text = it
            }
        })

        viewModel.realname.observe(viewLifecycleOwner, {
            it?.let {
                binding.realname.text = it
            }
        })

        viewModel.phone.observe(viewLifecycleOwner, {
            it?.let {
                binding.phone.text = it
            }
        })

        viewModel.address.observe(viewLifecycleOwner, {
            it?.let {
                binding.address.text = it
            }
        })

        viewModel.reason.observe(viewLifecycleOwner, {
            it?.let {
                binding.reason.text = it
            }
        })

        viewModel.error.observe(viewLifecycleOwner, {
            it?.let {
                Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                binding.approveButton.isEnabled = true
                binding.disapproveButton.isEnabled = true

                if (it.message == "Object is not found.") {
                    requireActivity().onBackPressed()
                }
            }
        })

        viewModel.finish.observe(viewLifecycleOwner, {
            it?.let {
                Toast.makeText(context, getString(R.string.done), Toast.LENGTH_SHORT).show()
                if (it) requireActivity().onBackPressed()
            }
        })

        binding.appBar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.approveButton.setOnClickListener {
            binding.approveButton.isEnabled = false
            binding.disapproveButton.isEnabled = false
            viewModel.approve(true)
        }

        binding.disapproveButton.setOnClickListener {
            binding.approveButton.isEnabled = false
            binding.disapproveButton.isEnabled = false
            viewModel.approve(false)
        }

        return binding.root
    }

}