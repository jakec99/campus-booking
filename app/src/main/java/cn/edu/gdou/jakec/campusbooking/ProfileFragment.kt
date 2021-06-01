package cn.edu.gdou.jakec.campusbooking

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import cn.edu.gdou.jakec.campusbooking.data.Role
import cn.edu.gdou.jakec.campusbooking.databinding.FragmentProfileBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

class ProfileFragment : Fragment() {

    private lateinit var viewModel: ProfileViewModel

    private lateinit var binding: FragmentProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_profile, container, false)

        viewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)

        viewModel.user.observe(viewLifecycleOwner, {
            it?.let {
                Glide.with(binding.image.context)
                    .load(it.imgUrl)
                    .apply(RequestOptions()
                        .placeholder(R.drawable.loading_animation)
                        .error(R.drawable.ic_broken_image))
                    .into(binding.image)
                binding.nickname.text = it.nickname
                binding.phone.text = it.phone
                binding.username.text = it.username
                binding.violation.text = it.violation.toString()
                binding.role.text = it.role.toString()
                if (it.role == Role.USER) {
                    binding.applyButton.visibility = View.VISIBLE
                }
            }
        })

        binding.appBar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.applyButton.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_manageStudyApplyFragment)
        }

        binding.logoutButton.setOnClickListener {
            viewModel.logout()
            findNavController().navigate(R.id.action_profileFragment_to_authFragment)
        }

        binding.imageBlock.setOnClickListener {
            //  todo: change image
        }

        binding.nickname.setOnClickListener {
            //  todo: change nickname, call dialog
        }

        return binding.root
    }

}