package cn.edu.gdou.jakec.campusbooking

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cn.edu.gdou.jakec.campusbooking.MyApplication.Companion.context
import cn.leancloud.AVCloud
import cn.leancloud.AVObject
import cn.leancloud.AVQuery
import cn.leancloud.AVUser
import cn.leancloud.types.AVGeoPoint
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import java.time.Instant
import java.util.*

class StudyOrderViewModel : ViewModel() {

    private val _order = MutableLiveData<List<String>>()
    val order: LiveData<List<String>>
        get() = _order

    private val _orderFinished = MutableLiveData<String>()
    val orderFinished: LiveData<String>
        get() = _orderFinished

    private val _error = MutableLiveData<String>()
    val error: LiveData<String>
        get() = _error

    private val _errorGet = MutableLiveData<String>()
    val errorGet: LiveData<String>
        get() = _errorGet

    private lateinit var roomName: String
    private lateinit var seatName: String
    private lateinit var roomId: String
    private lateinit var seatId: String
    private var isSitting: Boolean = false
    private var expireAt: Long = 0

    lateinit var orderId: String
    var fromScan = false


    fun getOrder() {
        var flag = false

        val query = AVQuery<AVObject>("StudyOrder")
        query.getInBackground(orderId).subscribe(object : Observer<AVObject> {
            override fun onSubscribe(d: Disposable) {}

            override fun onNext(t: AVObject) {
                flag = true
                setOrder(t)
            }

            override fun onError(e: Throwable) {
                flag = true
                _error.value = e.message
            }

            override fun onComplete() {
                if (!flag) _errorGet.value = context.getString(R.string.invalid_order)
            }
        })
    }

    private fun setOrder(t: AVObject) {
        roomName = t.getString("roomName")
        seatName = t.getString("seatName")
        roomId = t.getString("roomId")
        seatId = t.getString("seatId")
        isSitting = t.getBoolean("isSitting")
        expireAt = t.getLong("expireAt")

        val date = Date(expireAt * 1000)
        val hrs = date.hours
        val min = date.minutes
        val now = Instant.now().epochSecond

        if (expireAt < now) {
            val statusText = context.getString(R.string.the_order_is_expired)
            _order.value = listOf("expired", roomName, seatName, statusText)
        } else {
            if (!isSitting) {
                val statusText = context.getString(R.string.check_in_before) +
                        hrs.toString() + ":" + min.toString() +
                        context.getString(R.string.otherwise_the_order_will_expire)
                _order.value = listOf("notInUse", roomName, seatName, statusText)
            } else {
                val statusText = "In use. Click LEAVE before you have to leave the seat."
                _order.value = listOf("inUse", roomName, seatName, statusText)
            }
        }
    }

    fun checkIn(geo: AVGeoPoint) {
        val userId = AVUser.getCurrentUser().objectId

        val params: MutableMap<String, Any> = HashMap()
        params["userId"] = userId
        params["seatId"] = seatId
        params["roomId"] = roomId
        params["orderId"] = orderId
        params["geo"] = geo

        AVCloud.callFunctionInBackground<String>("studyCheckIn", params)
            .subscribe(object : Observer<String> {

                override fun onSubscribe(d: Disposable) {}

                override fun onNext(t: String) {
                    getOrder()
                }

                override fun onError(e: Throwable) {
                    _error.value = e.message
                }

                override fun onComplete() {}
            })
    }

    fun leave() {
        val userId = AVUser.getCurrentUser().objectId

        val params: MutableMap<String, Any> = HashMap()
        params["userId"] = userId
        params["seatId"] = seatId
        params["roomId"] = roomId
        params["orderId"] = orderId

        AVCloud.callFunctionInBackground<String>("studyLeave", params)
            .subscribe(object : Observer<String> {

                override fun onSubscribe(d: Disposable) {}

                override fun onNext(t: String) {
                    getOrder()
                }

                override fun onError(e: Throwable) {
                    _error.value = e.message
                }

                override fun onComplete() {}
            })
    }

    fun finish() {
        val params: MutableMap<String, Any> = HashMap()
        params["orderId"] = orderId

        AVCloud.callFunctionInBackground<String>("studyFinishOrder", params)
            .subscribe(object : Observer<String> {

                override fun onSubscribe(d: Disposable) {}

                override fun onNext(t: String) {
                    _orderFinished.value = t
                }

                override fun onError(e: Throwable) {
                    _error.value = e.message
                }

                override fun onComplete() {}
            })
    }

}