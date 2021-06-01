package cn.edu.gdou.jakec.campusbooking

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import cn.edu.gdou.jakec.campusbooking.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {

    companion object {
        fun newInstance() = LoginFragment()
    }

    private lateinit var binding: FragmentLoginBinding

//    private lateinit var viewModel: LoginViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_login,
            container,
            false
        )

        // Get the viewModel
//        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)

        binding.appBar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.sendButton.setOnClickListener { view: View ->
//            TODO
        }

        binding.loginButton.setOnClickListener { view: View ->
//            TODO
        }

//        binding.loginPwdButton.setOnClickListener { view : View ->
//            view.findNavController().navigate(R.id.action_loginFragment_to_loginPwdFragment)
//        }

        return binding.root
    }

}