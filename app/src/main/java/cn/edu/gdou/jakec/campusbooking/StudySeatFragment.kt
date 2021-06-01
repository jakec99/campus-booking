package cn.edu.gdou.jakec.campusbooking

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import cn.edu.gdou.jakec.campusbooking.databinding.FragmentStudySeatBinding
import cn.leancloud.types.AVGeoPoint
import timber.log.Timber

class StudySeatFragment : Fragment(), LocationListener {

    private lateinit var binding: FragmentStudySeatBinding

    private lateinit var viewModel: StudySeatViewModel

    private lateinit var locationManager: LocationManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_study_seat,
            container,
            false
        )

        viewModel = ViewModelProvider(this).get(StudySeatViewModel::class.java)
        viewModel.setOrder(StudySeatFragmentArgs.fromBundle(requireArguments()).seat)

        binding.appBar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }

        viewModel.seat.observe(viewLifecycleOwner, {
            it?.let {
                binding.roomName.text = it.roomName
                binding.seatName.text = it.name
                if (!it.isEnabled) {
                    Toast.makeText(context, getString(R.string.the_seat_is_unavailable),
                        Toast.LENGTH_SHORT).show()
                    requireActivity().onBackPressed()
                } else {
                    if (!it.isOccupied) {
                        binding.bookButton.visibility = View.VISIBLE
                        binding.status.text = getString(R.string.the_seat_is_available_now)
                    } else {
                        Toast.makeText(context,
                            getString(R.string.the_seat_is_occupied_by_someone_else),
                            Toast.LENGTH_SHORT).show()
                        requireActivity().onBackPressed()
                    }
                }

            }
        })

        viewModel.order.observe(viewLifecycleOwner, {
            it?.let {
                findNavController().navigate(StudySeatFragmentDirections
                    .actionStudySeatFragmentToStudyOrderFragment(it))
            }
        })

        viewModel.error.observe(viewLifecycleOwner, {
            it?.let {
                binding.bookButton.isEnabled = true
            }
        })

        binding.bookButton.setOnClickListener {
            getLocation()
        }

        return binding.root
    }

    private fun getLocation() {

        locationManager =
            activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        if ((ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) || (!locationManager.isProviderEnabled(
                LocationManager.GPS_PROVIDER))
        ) {

            AlertDialog.Builder(context)
                .setMessage(getString(R.string.your_location_is_needed_to_check_in))
                .setPositiveButton(getString(R.string.ok)) { dialog, which ->
                    getPermission()
                }
                .setNegativeButton(getString(R.string.cancel)) { dialog, which ->
                    Toast.makeText(context, getString(R.string.check_in_canceled),
                        Toast.LENGTH_SHORT).show()
                }
                .show()

        } else {
            binding.progressBar.visibility = View.VISIBLE
            binding.bookButton.isEnabled = false
            Toast.makeText(context, getString(R.string.start_to_check_in),
                Toast.LENGTH_SHORT).show()
            try {
                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    Timber.i("GPS provider")
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        0, 0.toFloat(), this)
                } else {
                    Timber.i("Network provider")
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                        0, 0.toFloat(), this)
                }
            } catch (e: Exception) {
                Timber.i(e)
            }
        }
    }

    private fun getPermission() {
        requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 34)
    }

    override fun onLocationChanged(location: Location) {
        locationManager.removeUpdates(this)
        val geo = AVGeoPoint(location.latitude, location.longitude)
        viewModel.createCheckIn(geo)
    }

}