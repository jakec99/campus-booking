package cn.edu.gdou.jakec.campusbooking

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import androidx.fragment.app.Fragment
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import cn.edu.gdou.jakec.campusbooking.databinding.FragmentLoginPwdBinding

class LoginPwdFragment : Fragment() {

    private lateinit var binding: FragmentLoginPwdBinding

    private lateinit var viewModel: LoginPwdViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_login_pwd,
            container,
            false
        )

        viewModel = ViewModelProvider(this).get(LoginPwdViewModel::class.java)

        val navController = findNavController()

        viewModel.loginSucceed.observe(viewLifecycleOwner, { loginSucceed ->
            if (loginSucceed) {
                val inputMethodManager = activity?.getSystemService(Context.INPUT_METHOD_SERVICE)
                        as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(requireView().getWindowToken(), 0)
                navController.navigate(R.id.action_loginPwdFragment_to_authFragment)
            } else {
                binding.loginButton.isEnabled = true
            }
        })

        viewModel.loginError.observe(viewLifecycleOwner, {
            it?.let {
                Toast.makeText(MyApplication.context, it.message, Toast.LENGTH_LONG).show()
            }
        })

        binding.appBar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }

        val afterTextChangedListener = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                binding.loginButton.isEnabled = false
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                val username = binding.usernameTextField.editText?.text.toString()
                val password = binding.passwordTextField.editText?.text.toString()
                binding.loginButton.isEnabled = !username.isEmpty() && !password.isEmpty()
            }
        }

        binding.usernameTextField.editText?.addTextChangedListener(afterTextChangedListener)
        binding.passwordTextField.editText?.addTextChangedListener(afterTextChangedListener)

        binding.loginButton.setOnClickListener { view: View ->
            login()
        }

        return binding.root
    }

    fun login() {
        val username = binding.usernameTextField.editText?.text.toString()
        val password = binding.passwordTextField.editText?.text.toString()
        if (username.isNotEmpty() || password.isNotEmpty()) {
            viewModel.login(username, password)
            binding.loginButton.isEnabled = false
        }
    }
}