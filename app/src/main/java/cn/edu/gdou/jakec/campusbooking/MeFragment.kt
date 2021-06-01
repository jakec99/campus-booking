package cn.edu.gdou.jakec.campusbooking

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import cn.edu.gdou.jakec.campusbooking.databinding.FragmentMeBinding

class MeFragment : Fragment() {

    private lateinit var binding: FragmentMeBinding

    private lateinit var viewModel: MeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_me,
            container,
            false
        )

        // Get the viewModel
        viewModel = ViewModelProvider(this).get(MeViewModel::class.java)

        val navController = findNavController()

        binding.logoutButton.setOnClickListener {
//            viewModel.testd()
            viewModel.logout()
            navController.navigate(R.id.action_studyFragment_to_authFragment)
        }

        return binding.root
    }
}