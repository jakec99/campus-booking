package cn.edu.gdou.jakec.campusbooking

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import cn.edu.gdou.jakec.campusbooking.databinding.FragmentManageStudyRoomCheckBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

class ManageStudyRoomCheckFragment : Fragment() {

    private lateinit var binding: FragmentManageStudyRoomCheckBinding

    private lateinit var viewModel: ManageStudyRoomCheckViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_manage_study_room_check,
            container,
            false
        )

        viewModel = ViewModelProvider(this).get(ManageStudyRoomCheckViewModel::class.java)
        viewModel.setTodo(ManageStudyRoomCheckFragmentArgs.fromBundle(requireArguments()).todo)

        viewModel.userImgUrl.observe(viewLifecycleOwner, {
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

        viewModel.imgUrl.observe(viewLifecycleOwner, {
            it?.let {
                Glide.with(binding.imageView.context)
                    .load(it)
                    .apply(RequestOptions()
                        .placeholder(R.drawable.loading_animation)
                        .error(R.drawable.ic_broken_image))
                    .into(binding.imageView)
            }
        })

        viewModel.name.observe(viewLifecycleOwner, {
            it?.let {
                binding.name.text = it
            }
        })

        viewModel.columns.observe(viewLifecycleOwner, {
            it?.let {
                binding.columns.text = it
            }
        })

        viewModel.rows.observe(viewLifecycleOwner, {
            it?.let {
                binding.rows.text = it
            }
        })

        viewModel.geo.observe(viewLifecycleOwner, {
            it?.let {
                binding.geo.text = it
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