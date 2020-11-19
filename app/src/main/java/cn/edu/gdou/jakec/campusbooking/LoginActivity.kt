package cn.edu.gdou.jakec.campusbooking

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import cn.edu.gdou.jakec.campusbooking.databinding.ActivityLoginBinding
import com.google.android.material.snackbar.Snackbar

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)

        binding.closeButton.setOnClickListener {
            Snackbar.make(it, "Don't want to close it.", Snackbar.LENGTH_SHORT)
                .show()
        }

        binding.sendButton.setOnClickListener {
            Snackbar.make(it, "Pretend to send a text.", Snackbar.LENGTH_SHORT)
                .show()
        }

        binding.registerButton.setOnClickListener {
            Snackbar.make(it, "Maybe login.", Snackbar.LENGTH_SHORT)
                .show()
        }

        binding.loginButton.setOnClickListener {
            Snackbar.make(it, "Waiting password.", Snackbar.LENGTH_SHORT)
                .show()
        }

        binding.passwordButton.setOnClickListener {
            binding.codeTextField.setVisibility(View.GONE);
            binding.sendButton.setVisibility(View.GONE);
            binding.registerButton.setVisibility(View.GONE);
            binding.orTextView.setVisibility(View.GONE);
            binding.passwordButton.setVisibility(View.GONE);
            binding.backButton.setVisibility(View.VISIBLE);
            binding.passwordTextField.setVisibility(View.VISIBLE);
            binding.loginButton.setVisibility(View.VISIBLE);
        }

        binding.backButton.setOnClickListener {
            binding.backButton.setVisibility(View.GONE);
            binding.passwordTextField.setVisibility(View.GONE);
            binding.loginButton.setVisibility(View.GONE);
            binding.codeTextField.setVisibility(View.VISIBLE);
            binding.sendButton.setVisibility(View.VISIBLE);
            binding.registerButton.setVisibility(View.VISIBLE);
            binding.orTextView.setVisibility(View.VISIBLE);
            binding.passwordButton.setVisibility(View.VISIBLE);
        }
    }
}