package cn.edu.gdou.jakec.campusbooking

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import cn.edu.gdou.jakec.campusbooking.databinding.FragmentAuthBinding

class AuthFragment : Fragment() {

    private lateinit var binding: FragmentAuthBinding

    private lateinit var viewModel: AuthViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_auth,
            container,
            false
        )

        // Get the viewModel
        viewModel = ViewModelProvider(this).get(AuthViewModel::class.java)

        // Add observer
        viewModel.isLoggedIn.observe(viewLifecycleOwner, { isLoggedIn ->
            if (isLoggedIn) {
                findNavController().navigate(AuthFragmentDirections.actionAuthFragmentToStudyFragment())
            } else {
                binding.titleText.visibility = View.VISIBLE
                binding.titleText2.visibility = View.VISIBLE
                binding.loginButton.visibility = View.VISIBLE
                binding.registerButton.visibility = View.VISIBLE
            }
        })

        binding.loginButton.setOnClickListener { view: View ->
            findNavController().navigate(AuthFragmentDirections.actionAuthFragmentToLoginPwdFragment())
        }

        binding.registerButton.setOnClickListener { view: View ->
            findNavController().navigate(AuthFragmentDirections.actionAuthFragmentToSignupFragment())
        }

        return binding.root
    }

}