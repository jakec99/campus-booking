package cn.edu.gdou.jakec.campusbooking

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import cn.edu.gdou.jakec.campusbooking.databinding.FragmentStudyOrderBinding
import cn.edu.gdou.jakec.campusbooking.utility.toAVGeoPoint
import cn.edu.gdou.jakec.campusbooking.utility.toText
import cn.leancloud.types.AVGeoPoint
import com.google.android.gms.location.*
import com.google.android.material.snackbar.Snackbar
import timber.log.Timber
import java.util.concurrent.TimeUnit


class StudyOrderFragment : Fragment() {

    //  Location Provider
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private var currentLocation: Location? = null

    private lateinit var binding: FragmentStudyOrderBinding

    private lateinit var viewModel: StudyOrderViewModel

    private lateinit var locationManager: LocationManager

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
                viewModel.checkIn(currentLocation?.toAVGeoPoint()!!)
                fusedLocationProviderClient.removeLocationUpdates(locationCallback)
                binding.scanButton.isEnabled = true
            }
        }

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_study_order,
            container,
            false
        )

        // Get the viewModel
        viewModel = ViewModelProvider(this).get(StudyOrderViewModel::class.java)

        val navController = findNavController()

        viewModel.orderId = StudyOrderFragmentArgs.fromBundle(requireArguments()).id
        viewModel.fromScan = StudyOrderFragmentArgs.fromBundle(requireArguments()).fromScan

        getOrder()

        viewModel.order.observe(viewLifecycleOwner, {
            it?.let {
                when {
                    it[0] == "notInUse" -> setOrderNotInUse(it[1], it[2], it[3])
                    it[0] == "inUse" -> setOrderInUse(it[1], it[2], it[3])
                    it[0] == "expired" -> setOrderExpired()
                }
            }
        })

        viewModel.orderFinished.observe(viewLifecycleOwner, {
            it?.let {
                navController
                    .navigate(StudyOrderFragmentDirections
                        .actionStudyOrderFragmentToStudyRatingFragment(it))
            }
        })

        viewModel.error.observe(viewLifecycleOwner, {
            it.let {
                AlertDialog.Builder(context)
                    .setMessage(it)
                    .setPositiveButton(getString(R.string.ok)) { dialog, which -> }
                    .show()
            }
            onError()
        })

        viewModel.errorGet.observe(viewLifecycleOwner, {
            it?.let {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                requireActivity().onBackPressed()
            }
        })

        binding.appBar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.scanButton.setOnClickListener {
            navController
                .navigate(StudyOrderFragmentDirections
                    .actionStudyOrderFragmentToScanFragment())
        }

        binding.leaveButton.setOnClickListener {
            leave()
        }

        binding.findButton.setOnClickListener {
//            todo
        }

        binding.finishButton.setOnClickListener {
            AlertDialog.Builder(context)
                .setMessage(getString(R.string.you_are_trying_to_finish_the_order))
                .setNegativeButton(getString(R.string.yes)) { dialog, which ->
                    finish()
                }
                .setPositiveButton(getString(R.string.cancel)) { dialog, which -> }
                .show()
        }

        return binding.root
    }

    private fun getOrder() {
        binding.progressBar.visibility = View.VISIBLE
        viewModel.getOrder()
    }

    private fun checkIn() {
        binding.scanButton.isEnabled = false
        binding.findButton.isEnabled = false
        binding.finishButton.isEnabled = false
        binding.progressBar.visibility = View.VISIBLE

        // Checks and requests if needed.
        if (permissionApproved()) {
            requestLocationUpdates()
            binding.scanButton.isEnabled = false
        } else {
            requestPermissions()
        }
    }

    private fun leave() {
        binding.leaveButton.isEnabled = false
        binding.findButton.isEnabled = false
        binding.finishButton.isEnabled = false
        binding.progressBar.visibility = View.VISIBLE

        viewModel.leave()
    }

    private fun finish() {
        binding.scanButton.isEnabled = false
        binding.leaveButton.isEnabled = false
        binding.findButton.isEnabled = false
        binding.finishButton.isEnabled = false
        binding.progressBar.visibility = View.VISIBLE

        viewModel.finish()
    }

    private fun setOrderNotInUse(roomName: String, seatName: String, statusText: String) {
        binding.roomName.text = roomName
        binding.seatName.text = seatName
        binding.statusText.text = statusText
        binding.scanButton.visibility = View.VISIBLE
        binding.findButton.visibility = View.VISIBLE
        binding.finishButton.visibility = View.VISIBLE
        binding.scanButton.isEnabled = true
        binding.findButton.isEnabled = true
        binding.finishButton.isEnabled = true

        binding.leaveButton.visibility = View.GONE
        binding.progressBar.visibility = View.GONE

        if (viewModel.fromScan) {
            checkIn()
        }
    }

    private fun setOrderInUse(roomName: String, seatName: String, statusText: String) {
        binding.roomName.text = roomName
        binding.seatName.text = seatName
        binding.statusText.text = statusText
        binding.leaveButton.visibility = View.VISIBLE
        binding.findButton.visibility = View.VISIBLE
        binding.finishButton.visibility = View.VISIBLE
        binding.leaveButton.isEnabled = true
        binding.findButton.isEnabled = true
        binding.finishButton.isEnabled = true

        binding.scanButton.visibility = View.GONE
        binding.progressBar.visibility = View.GONE

    }

    private fun setOrderExpired() {
        Toast.makeText(context, getString(R.string.the_order_is_expired), Toast.LENGTH_SHORT).show()
        requireActivity().onBackPressed()
    }

    private fun onError() {
        binding.scanButton.isEnabled = true
        binding.leaveButton.isEnabled = true
        binding.findButton.isEnabled = true
        binding.finishButton.isEnabled = true
        binding.progressBar.visibility = View.GONE
    }

    private fun requestLocationUpdates() {
        try {
            fusedLocationProviderClient.requestLocationUpdates(
                locationRequest, locationCallback, Looper.getMainLooper())
        } catch (unlikely: SecurityException) {
            Timber.i("Lost location permissions. Couldn't remove updates. $unlikely")
            binding.scanButton.isEnabled = true
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