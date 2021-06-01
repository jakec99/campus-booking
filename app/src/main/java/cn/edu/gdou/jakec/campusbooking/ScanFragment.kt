package cn.edu.gdou.jakec.campusbooking

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import cn.edu.gdou.jakec.campusbooking.databinding.FragmentScanBinding
import com.google.zxing.integration.android.IntentIntegrator
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import timber.log.Timber

class ScanFragment : Fragment() {

    private lateinit var binding: FragmentScanBinding

    private lateinit var viewModel: ScanViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_scan,
            container,
            false
        )

        viewModel = ViewModelProvider(this).get(ScanViewModel::class.java)

        val navController = findNavController()

        initializeScan()

        viewModel.error.observe(viewLifecycleOwner, {
            it?.let {
                Toast.makeText(MyApplication.context, it, Toast.LENGTH_SHORT).show()
                requireActivity().onBackPressed()
            }
        })

        viewModel.seat.observe(viewLifecycleOwner, {
            it?.let {
                navController.navigate(ScanFragmentDirections
                    .actionScanFragmentToStudySeatFragment(it))
            }
        })

        viewModel.orderId.observe(viewLifecycleOwner, {
            it?.let {
                val action = ScanFragmentDirections
                    .actionScanFragmentToStudyOrderFragment(it)
                action.fromScan = true
                navController.navigate(action)
            }
        })

        return binding.root
    }

    private fun initializeScan() {
        val integrator = IntentIntegrator.forSupportFragment(this)
        integrator.setPrompt(getString(R.string.scan_the_qr_code_of_the_seat))
        integrator.setBeepEnabled(true)
        integrator.setOrientationLocked(false)
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
        integrator.initiateScan()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            viewModel.handleResult(result)
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

}