package cn.edu.gdou.jakec.campusbooking

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import cn.edu.gdou.jakec.campusbooking.databinding.ActivityLoginBinding
import androidx.databinding.DataBindingUtil
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)
    }
}