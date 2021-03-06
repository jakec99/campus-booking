package cn.edu.gdou.jakec.campusbooking

import android.Manifest
import android.app.Activity
import android.content.*
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.provider.MediaStore
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import cn.edu.gdou.jakec.campusbooking.databinding.FragmentManageStudyRoomApplyBinding
import cn.edu.gdou.jakec.campusbooking.utility.toText
import com.google.android.gms.location.*
import com.google.android.material.snackbar.Snackbar
import timber.log.Timber
import java.io.IOException
import java.util.concurrent.TimeUnit

class ManageStudyRoomApplyFrament : Fragment() {

    //  Location Provider
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private var currentLocation: Location? = null

    private lateinit var binding: FragmentManageStudyRoomApplyBinding

    private lateinit var viewModel: ManageStudyRoomApplyViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        //  Location Provider
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireContext())
        locationRequest = LocationRequest.create().apply {
            interval = TimeUnit.SECONDS.toMillis(2)
            fastestInterval = TimeUnit.SECONDS.toMillis(1)
            maxWaitTime = TimeUnit.SECONDS.toMillis(10)
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                currentLocation = locationResult.lastLocation
                currentLocation?.let {
                    fusedLocationProviderClient.removeLocationUpdates(locationCallback)
                    viewModel.setLocation(it)
                    binding.geoTextField.editText?.setText(currentLocation?.toText())
                    binding.geoButton.isEnabled = true
                }
            }
        }

        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_manage_study_room_apply, container, false)

        viewModel = ViewModelProvider(this).get(ManageStudyRoomApplyViewModel::class.java)

        viewModel.error.observe(viewLifecycleOwner, {
            it?.let {
                Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                binding.submitButton.isEnabled = true
            }
        })

        viewModel.finish.observe(viewLifecycleOwner, {
            it?.let {
                if (it) {
                    Toast.makeText(context,
                        getString(R.string.your_application_has_been_sent),
                        Toast.LENGTH_SHORT).show()
                    requireActivity().onBackPressed()
                }
            }
        })

        binding.appBar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.imageView.setOnClickListener {
            selectImage()
        }

        binding.geoButton.setOnClickListener {
            // Checks and requests if needed.
            if (permissionApproved()) {
                requestLocationUpdates()
                it.isEnabled = false
            } else {
                requestPermissions()
            }
        }

        binding.submitButton.setOnClickListener {
            if (isNotEmpty()) {
                val inputMethodManager = activity?.getSystemService(Context.INPUT_METHOD_SERVICE)
                        as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(requireView().getWindowToken(), 0)
                binding.submitButton.isEnabled = false
                viewModel.uploadImage(binding.name.editText?.text.toString(),
                    binding.columns.editText?.text.toString(),
                    binding.rows.editText?.text.toString()
                )
            } else {
                Toast.makeText(context,
                    getString(R.string.the_contents_cant_be_null),
                    Toast.LENGTH_SHORT).show()
            }
        }

        return binding.root
    }

    private fun isNotEmpty(): Boolean {
        return (binding.name.editText?.text.toString()
            .isNotEmpty() && binding.columns.editText?.text.toString()
            .isNotEmpty() && binding.rows.editText?.text.toString()
            .isNotEmpty())
    }

    //  Image
    fun selectImage() {
        val intent = Intent()
        intent.setType("image/*")
        intent.setAction(Intent.ACTION_GET_CONTENT)
        startActivityForResult(Intent.createChooser(intent, getString(R.string.select_a_photo)), 22)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 22 && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            val filePath = data.getData()
            if (filePath != null) {
                viewModel.setUri(filePath)
            }
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(context?.contentResolver, filePath)
                binding.imageView.setImageBitmap(bitmap)
            } catch (e: IOException) {
                viewModel.setError(e)
            }

        }
    }

    //  Location
    private fun requestLocationUpdates() {
        try {
            fusedLocationProviderClient.requestLocationUpdates(
                locationRequest, locationCallback, Looper.getMainLooper())
        } catch (unlikely: SecurityException) {
            Timber.i("Lost location permissions. Couldn't remove updates. $unlikely")
            binding.geoButton.isEnabled = true
        }
    }

    // Method checks if permissions approved.
    private fun permissionApproved(): Boolean {
        return PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }

    // Method requests permissions.
    private fun requestPermissions() {
        val provideRationale = permissionApproved()

        // If the user denied a previous request, but didn't check "Don't ask again", provide
        // additional rationale.
        if (provideRationale) {
            Snackbar.make(
                binding.layout,
                getString(R.string.location_permission_needed),
                Snackbar.LENGTH_LONG
            )
                .setAction(R.string.ok) {
                    // Request permission
                    ActivityCompat.requestPermissions(
                        requireActivity(),
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        34
                    )
                }
                .show()
        } else {
            Timber.i("Request permission")
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                34
            )
        }
    }

    // Handles permission result
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray,
    ) {
        Timber.i("onRequestPermissionResult")

        when (requestCode) {
            34 -> when {
                grantResults.isEmpty() ->
                    // If user interaction was interrupted, the permission request
                    // is cancelled and you receive empty arrays.
                    Timber.i("User interaction was cancelled.")
                grantResults[0] == PackageManager.PERMISSION_GRANTED ->
                    // Permission was granted.
                    requestLocationUpdates()
                else -> {
                    // Permission denied.
                    Snackbar.make(
                        binding.layout,
                        getString(R.string.your_location_is_needed_to_check_in),
                        Snackbar.LENGTH_LONG
                    )
                        .setAction(R.string.settings) {
                            // Build intent that displays the App settings screen.
                            val intent = Intent()
                            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                            val uri = Uri.fromParts(
                                "package",
                                BuildConfig.APPLICATION_ID,
                                null
                            )
                            intent.data = uri
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                        }
                        .show()
                }
            }
        }
    }

}