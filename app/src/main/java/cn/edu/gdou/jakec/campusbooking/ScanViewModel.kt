package cn.edu.gdou.jakec.campusbooking

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cn.edu.gdou.jakec.campusbooking.data.StudySeat
import cn.leancloud.AVObject
import cn.leancloud.AVQuery
import cn.leancloud.AVUser
import com.google.zxing.integration.android.IntentResult
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import java.time.Instant

class ScanViewModel : ViewModel() {

    private val _seat = MutableLiveData<StudySeat>()
    val seat: LiveData<StudySeat> get() = _seat

    private val _orderId = MutableLiveData<String>()
    val orderId: LiveData<String> get() = _orderId

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    private lateinit var studySeat: StudySeat

    fun handleResult(result: IntentResult) {
        if (result.contents == null) {
            _error.value = MyApplication.context.getString(R.string.scan_canceled)
        } else {
            if (result.contents.length == 25) {
                if (result.contents[0] == 's') {
                    getSeat(result.contents.substring(1))
                    return
                }
            }
            _error.value = MyApplication.context.getString(R.string.invalid_qr_code)
        }
    }

    fun getSeat(seatId: String) {
        var flag = false
        val query = AVQuery<AVObject>("StudySeat")
        query.getInBackground(seatId).subscribe(object : Observer<AVObject> {
            override fun onSubscribe(d: Disposable) {}
            override fun onNext(t: AVObject) {
                flag = true
                if (!t.getBoolean("isOccupied") || !t.getBoolean("isEnabled")) {
                    studySeat = StudySeat(
                        xIndex = t.getInt("xIndex"),
                        yIndex = t.getInt("yIndex"),
                        id = t.objectId,
                        type = t.getString("type"),
                        name = t.getString("name"),
                        isEnabled = t.getBoolean("isEnabled"),
                        isOccupied = t.getBoolean("isOccupied"),
                        roomName = t.getString("roomName"),
                        roomId = t.getString("roomId")
                    )
                    _seat.value = studySeat
                } else {
                    getOrder(seatId)
                }
            }

            override fun onError(e: Throwable) {
                flag = true
                _error.value = e.message
            }

            override fun onComplete() {
                if (!flag) _error.value = MyApplication.context.getString(R.string.invalid_qr_code)
            }
        })
    }

    fun getOrder(seatId: String) {
        var flag = false
        val now = Instant.now().epochSecond
        val userId = AVUser.currentUser().objectId

        val query = AVQuery<AVObject>("StudyOrder")
        query.whereEqualTo("seatId", seatId)
        query.whereGreaterThanOrEqualTo("expireAt", now)
        query.firstInBackground.subscribe(object : Observer<AVObject> {
            override fun onSubscribe(d: Disposable) {}
            override fun onNext(t: AVObject) {
                flag = true

                if (userId != t.getString("userId")) {
//                    The seat is occupied by someone else
                    _seat.value = studySeat
                } else {
                    _orderId.value = t.objectId
                }
            }

            override fun onError(e: Throwable) {
                flag = true
                _error.value = e.message
            }

            override fun onComplete() {
                if (!flag) _error.value = MyApplication.context.getString(R.string.invalid_qr_code)
            }
        })
    }
}