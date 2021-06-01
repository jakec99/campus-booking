package cn.edu.gdou.jakec.campusbooking

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cn.edu.gdou.jakec.campusbooking.data.StudyOrder
import cn.edu.gdou.jakec.campusbooking.data.StudySeat
import cn.leancloud.AVCloud
import cn.leancloud.AVObject
import cn.leancloud.AVUser
import cn.leancloud.types.AVGeoPoint
import io.reactivex.Observer
import io.reactivex.disposables.Disposable

class StudySeatViewModel : ViewModel() {

    private val _seat = MutableLiveData<StudySeat>()
    val seat: LiveData<StudySeat> get() = _seat

    private val _order = MutableLiveData<String>()
    val order: LiveData<String> get() = _order

    private val _error = MutableLiveData<Throwable>()
    val error: LiveData<Throwable> get() = _error

    private lateinit var studySeat: StudySeat

    fun setOrder(seat: StudySeat) {
        studySeat = seat
        _seat.value = seat
    }

    fun createCheckIn(geo: AVGeoPoint) {
        val user = AVUser.getCurrentUser()

        val params: MutableMap<String, Any> = HashMap()
        params["userName"] = user.username
        params["userId"] = user.objectId
        params["seatId"] = studySeat.id
        params["roomId"] = studySeat.roomId
        params["geo"] = geo

        AVCloud.callFunctionInBackground<String>("studyOrderCreateCheckIn", params)
            .subscribe(object : Observer<String> {
                override fun onSubscribe(d: Disposable) {}
                override fun onNext(t: String) {
                    _order.value = t
                }

                override fun onError(e: Throwable) {
                    _error.value = e
                }

                override fun onComplete() {}
            })
    }

}