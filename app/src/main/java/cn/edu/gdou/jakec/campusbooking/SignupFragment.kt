package cn.edu.gdou.jakec.campusbooking

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import cn.edu.gdou.jakec.campusbooking.databinding.FragmentSignupBinding

class SignupFragment : Fragment() {

    private lateinit var binding: FragmentSignupBinding

    private lateinit var viewModel: SignupViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_signup,
            container,
            false
        )

        viewModel = ViewModelProvider(this).get(SignupViewModel::class.java)

        viewModel.hasSent.observe(viewLifecycleOwner, {
            it?.let {
                if (it) {
                    Toast.makeText(context, getString(R.string.code_sent), Toast.LENGTH_SHORT)
                        .show()
                }
            }
        })

        viewModel.hasLogined.observe(viewLifecycleOwner, {
            it?.let {
                if (it) {
                    val inputMethodManager =
                        activity?.getSystemService(Context.INPUT_METHOD_SERVICE)
                                as InputMethodManager
                    inputMethodManager.hideSoftInputFromWindow(requireView().getWindowToken(), 0)
                    findNavController().navigate(R.id.action_signupFragment_to_authFragment)
                }
            }
        })

        viewModel.error.observe(viewLifecycleOwner, {
            it?.let {
                Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                binding.button.isEnabled = true
            }
        })

        binding.appBar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.codeButton.setOnClickListener {
            if (binding.phone.editText?.text?.isNotEmpty() == true) {
                binding.codeButton.isEnabled = false
                viewModel.sendCode(binding.phone.editText?.text.toString())
            } else {
                Toast.makeText(context,
                    getString(R.string.the_phone_number_cant_be_null),
                    Toast.LENGTH_SHORT).show()
            }
        }

        binding.button.setOnClickListener {
            if (binding.phone.editText?.text?.isNotEmpty() == true && binding.code.editText?.text?.isNotEmpty() == true) {
                binding.button.isEnabled = false
                viewModel.signupOrLogin(binding.phone.editText?.text.toString(),
                    binding.code.editText?.text.toString())
            } else {
                Toast.makeText(context,
                    getString(R.string.the_contents_cant_be_null),
                    Toast.LENGTH_SHORT).show()
            }
        }

        return binding.root
    }
}