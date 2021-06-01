package cn.edu.gdou.jakec.campusbooking

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cn.leancloud.AVCloud
import cn.leancloud.AVObject
import cn.leancloud.AVQuery
import cn.leancloud.AVUser
import cn.leancloud.types.AVGeoPoint
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import java.util.HashMap

class ManageStudySeatEditViewModel : ViewModel() {

    private val _finished = MutableLiveData<Boolean>()
    val finished: LiveData<Boolean> get() = _finished

    private val _name = MutableLiveData<String>()
    val name: LiveData<String> get() = _name

    private val _error = MutableLiveData<Throwable>()
    val error: LiveData<Throwable> get() = _error

    private lateinit var seatId: String

    fun getSeat(seatId: String) {
        this.seatId = seatId
        val query = AVQuery<AVObject>("StudySeat")
        query.getInBackground(seatId).subscribe(object : Observer<AVObject> {
            override fun onSubscribe(d: Disposable) {}

            override fun onNext(t: AVObject) {
                _name.value = t.getString("name")
            }

            override fun onError(e: Throwable) {
                _error.value = e
            }

            override fun onComplete() {}

        })
    }

    fun edit(name: String) {
        val params: MutableMap<String, Any> = HashMap()
        params["seatId"] = seatId
        params["seatName"] = name
        AVCloud.callFunctionInBackground<String>("studySeatEdit", params)
            .subscribe(object : Observer<String> {

                override fun onSubscribe(d: Disposable) {}

                override fun onNext(t: String) {
                    _finished.value = true
                }

                override fun onError(e: Throwable) {
                    _error.value = e
                }

                override fun onComplete() {}
            })

    }

    fun delete() {

    }


}