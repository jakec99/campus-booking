package cn.edu.gdou.jakec.campusbooking

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cn.edu.gdou.jakec.campusbooking.MyApplication.Companion.context
import cn.edu.gdou.jakec.campusbooking.data.StudyOrder
import cn.leancloud.AVCloud
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import java.util.*

class ManageStudyOrderViewModel : ViewModel() {

    private val _error = MutableLiveData<Throwable>()
    val error: LiveData<Throwable> get() = _error

    private val _order = MutableLiveData<StudyOrder>()
    val order: LiveData<StudyOrder> get() = _order

    private val _finished = MutableLiveData<Boolean>()
    val finished: LiveData<Boolean> get() = _finished

    private lateinit var orderId: String

    fun setOrder(order: StudyOrder) {
        orderId = order.orderId
        _order.value = order
    }

    fun finish() {
        val params: MutableMap<String, Any> = HashMap()
        params["orderId"] = orderId

        AVCloud.callFunctionInBackground<String>("studyFinishOrder", params)
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