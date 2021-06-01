package cn.edu.gdou.jakec.campusbooking

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cn.leancloud.AVCloud
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import java.util.HashMap

class ManageStudySeatCreateViewModel : ViewModel() {

    private val _finished = MutableLiveData<Boolean>()
    val finished: LiveData<Boolean> get() = _finished

    private val _error = MutableLiveData<Throwable>()
    val error: LiveData<Throwable> get() = _error

    lateinit var roomId: String
    var xIndex = 0
    var yIndex = 0

    fun create(name: String) {
        val params: MutableMap<String, Any> = HashMap()
        params["roomId"] = roomId
        params["name"] = name
        params["xIndex"] = xIndex
        params["yIndex"] = yIndex

        AVCloud.callFunctionInBackground<String>("studyCreateSeat", params)
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


}