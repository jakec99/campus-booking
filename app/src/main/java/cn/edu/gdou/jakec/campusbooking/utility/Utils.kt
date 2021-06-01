package cn.edu.gdou.jakec.campusbooking.utility

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.net.Uri
import android.widget.TextView
import androidx.core.content.edit
import androidx.databinding.BindingAdapter
import cn.edu.gdou.jakec.campusbooking.MyApplication
import cn.edu.gdou.jakec.campusbooking.R
import cn.edu.gdou.jakec.campusbooking.data.StudyRoom
import cn.leancloud.types.AVGeoPoint
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("SimpleDateFormat")
fun Date?.toText(): String {
    return if (this != null) {
        SimpleDateFormat("yyyy-MM-dd E hh:mm").format(this)
    } else {
        "Unknown Date"
    }
}

fun Location?.toText(): String {
    return if (this != null) {
        "($latitude, $longitude)"
    } else {
        "Unknown location"
    }
}

fun Location?.toAVGeoPoint(): AVGeoPoint {
    return if (this != null) {
        AVGeoPoint(latitude, longitude)
    } else {
        AVGeoPoint(0.toDouble(), 0.toDouble())
    }
}

internal object SharedPreferenceUtil {

    const val KEY_FOREGROUND_ENABLED = "tracking_foreground_location"

    /**
     * Returns true if requesting location updates, otherwise returns false.
     *
     * @param context The [Context].
     */
    fun getLocationTrackingPref(context: Context): Boolean =
        context.getSharedPreferences(
            context.getString(R.string.preference_file_key), Context.MODE_PRIVATE)
            .getBoolean(KEY_FOREGROUND_ENABLED, false)

    /**
     * Stores the location updates state in SharedPreferences.
     * @param requestingLocationUpdates The location updates state.
     */
    fun saveLocationTrackingPref(context: Context, requestingLocationUpdates: Boolean) =
        context.getSharedPreferences(
            context.getString(R.string.preference_file_key),
            Context.MODE_PRIVATE).edit {
            putBoolean(KEY_FOREGROUND_ENABLED, requestingLocationUpdates)
        }
}

fun readBytes(uri: Uri): ByteArray? =
    MyApplication.context.contentResolver.openInputStream(uri)?.buffered()?.use { it.readBytes() }
