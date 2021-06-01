package cn.edu.gdou.jakec.campusbooking.service

import android.app.Service
import android.content.Intent
import android.location.Location
import android.os.Binder
import android.os.IBinder
import android.os.Looper
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import cn.edu.gdou.jakec.campusbooking.utility.SharedPreferenceUtil
import com.google.android.gms.location.*
import timber.log.Timber
import java.util.concurrent.TimeUnit

class OneTimeLocationService : Service() {

    private val localBinder = LocalBinder()

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private lateinit var locationRequest: LocationRequest

    private lateinit var locationCallback: LocationCallback

    private var currentLocation: Location? = null

    override fun onCreate() {

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        locationRequest = LocationRequest.create().apply {
            interval = TimeUnit.SECONDS.toMillis(60)
            fastestInterval = TimeUnit.SECONDS.toMillis(30)
            maxWaitTime = TimeUnit.MINUTES.toMillis(2)
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)

                currentLocation = locationResult.lastLocation

                val intent = Intent(ACTION_ONE_TIME_LOCATION_BROADCAST)
                intent.putExtra(EXTRA_LOCATION, currentLocation)
                LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)
            }
        }
    }

    override fun onBind(intent: Intent): IBinder {
        stopForeground(true)
        return localBinder
    }

    fun subscribeToLocationUpdates() {

        SharedPreferenceUtil.saveLocationTrackingPref(this, true)

        startService(Intent(applicationContext, OneTimeLocationService::class.java))

        try {
            // Subscribe to location changes
            fusedLocationProviderClient.requestLocationUpdates(
                locationRequest, locationCallback, Looper.getMainLooper())
        } catch (unlikely: SecurityException) {
            SharedPreferenceUtil.saveLocationTrackingPref(this, false)
            Timber.i("Lost location permissions. Couldn't remove updates. $unlikely")
        }
    }

    fun unsubscribeToLocationUpdates() {

        try {
            // Unsubscribe to location changes.
            val removeTask = fusedLocationProviderClient.removeLocationUpdates(locationCallback)
            removeTask.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Timber.i("Location Callback removed.")
                    stopSelf()
                } else {
                    Timber.i("Failed to remove Location Callback.")
                }
            }
            SharedPreferenceUtil.saveLocationTrackingPref(this, false)
        } catch (unlikely: SecurityException) {
            SharedPreferenceUtil.saveLocationTrackingPref(this, true)
            Timber.i("Lost location permissions. Couldn't remove updates. $unlikely")
        }
    }

    inner class LocalBinder : Binder() {
        internal val service: OneTimeLocationService
            get() = this@OneTimeLocationService
    }

    companion object {
        private const val PACKAGE_NAME = "cn.edu.gdou.jakec.campusbooking"

        internal const val ACTION_ONE_TIME_LOCATION_BROADCAST =
            "$PACKAGE_NAME.action.ONE_TIME_LOCATION_BROADCAST"

        internal const val EXTRA_LOCATION = "$PACKAGE_NAME.extra.LOCATION"

    }


}