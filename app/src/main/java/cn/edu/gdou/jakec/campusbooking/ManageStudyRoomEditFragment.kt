package cn.edu.gdou.jakec.campusbooking

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import cn.edu.gdou.jakec.campusbooking.databinding.FragmentManageStudyRoomEditBinding
import cn.leancloud.types.AVGeoPoint
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import timber.log.Timber
import java.io.IOException

class ManageStudyRoomEditFragment : Fragment(), LocationListener {

    private lateinit var binding: FragmentManageStudyRoomEditBinding

    private lateinit var viewModel: ManageStudyRoomEditViewModel

    private lateinit var locationManager: LocationManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_manage_study_room_edit,
            container,
            false
        )

        viewModel = ViewModelProvider(this).get(ManageStudyRoomEditViewModel::class.java)

        viewModel.getRoom(ManageStudyRoomEditFragmentArgs.fromBundle(requireArguments()).id)

        viewModel.room.observe(viewLifecycleOwner, {
            it?.let {
                binding.button.isEnabled = true
                binding.nameTextField.editText?.setText(it.name)
                binding.xCountTextField.editText?.setText(it.xCount.toString())
                binding.yCountTextField.editText?.setText(it.yCount.toString())
                val geo = it.latitude.toString() + ", " + it.longitude.toString()
                binding.geoTextField.editText?.setText(geo)
                Glide.with(binding.imageView.context)
                    .load(it.imgUrl)
                    .apply(RequestOptions()
                        .placeholder(R.drawable.loading_animation)
                        .error(R.drawable.ic_broken_image))
                    .into(binding.imageView)
            }
        })

        viewModel.finished.observe(viewLifecycleOwner, {
            it?.let {
                if (it) {
                    Toast.makeText(context, getString(R.string.done), Toast.LENGTH_SHORT).show()
                    requireActivity().onBackPressed()
                }
            }
        })

        viewModel.error.observe(viewLifecycleOwner, {
            it?.let {
                Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                binding.geoButton.isEnabled = true
                binding.button.isEnabled = true
                binding.deleteButton.isEnabled = true
            }
        })

        binding.appBar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.imageView.setOnClickListener {
            selectImage()
        }

        binding.geoButton.setOnClickListener {
            binding.geoButton.isEnabled = false
            getLocation()
        }

        binding.button.setOnClickListener {
            binding.button.isEnabled = false
            viewModel.setName(binding.nameTextField.editText?.text.toString())
            viewModel.setXCount(binding.xCountTextField.editText?.text.toString().toInt())
            viewModel.setYCount(binding.yCountTextField.editText?.text.toString().toInt())
            viewModel.uploadImage()
        }

        binding.deleteButton.setOnClickListener {
            binding.deleteButton.isEnabled = false
            viewModel.delete()
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

            binding.geoButton.isEnabled = true
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
        binding.geoButton.isEnabled = true
        locationManager.removeUpdates(this)
        val str = location.latitude.toString() + ", " + location.longitude.toString()
        binding.geoTextField.editText?.setText(str)
        viewModel.setGeo(AVGeoPoint(location.latitude, location.longitude))
    }

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

}