package cn.edu.gdou.jakec.campusbooking

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cn.edu.gdou.jakec.campusbooking.data.ManageTodo
import cn.leancloud.AVCloud
import cn.leancloud.AVObject
import cn.leancloud.AVQuery
import io.reactivex.Observer
import io.reactivex.disposables.Disposable

class ManageStudyCheckViewModel : ViewModel() {

    private val _imgUrl = MutableLiveData<String>()
    val imgUrl: LiveData<String> get() = _imgUrl

    private val _nickname = MutableLiveData<String>()
    val nickname: LiveData<String> get() = _nickname

    private val _realname = MutableLiveData<String>()
    val realname: LiveData<String> get() = _realname

    private val _phone = MutableLiveData<String>()
    val phone: LiveData<String> get() = _phone

    private val _address = MutableLiveData<String>()
    val address: LiveData<String> get() = _address

    private val _reason = MutableLiveData<String>()
    val reason: LiveData<String> get() = _reason

    private val _finish = MutableLiveData<Boolean>()
    val finish: LiveData<Boolean> get() = _finish

    private val _error = MutableLiveData<Throwable>()
    val error: LiveData<Throwable> get() = _error

    private lateinit var applicationId: String

    fun setTodo(todo: ManageTodo) {
        applicationId = todo.id
        _imgUrl.value = todo.imgUrl
        _nickname.value = todo.nickname
        val query = AVQuery<AVObject>("StudyManagerApplication")
        query.getInBackground(todo.id).subscribe(object : Observer<AVObject> {
            override fun onSubscribe(d: Disposable) {}

            override fun onNext(t: AVObject) {
                _realname.value = t.getString("realname")
                _phone.value = t.getString("phone")
                _address.value = t.getString("address")
                _reason.value = t.getString("reason")
            }

            override fun onError(e: Throwable) {
                _error.value = e
            }

            override fun onComplete() {}

        })

    }

    fun approve(approve: Boolean) {
        val params: MutableMap<String, Any> = HashMap()
        params["applicationId"] = applicationId
        params["approve"] = approve
        AVCloud.callFunctionInBackground<String>("manageStudyManagerApprove", params)
            .subscribe(object : Observer<String> {
                override fun onSubscribe(d: Disposable) {}

                override fun onNext(t: String) {
                    _finish.value = true
                }

                override fun onError(e: Throwable) {
                    _error.value = e
                }

                override fun onComplete() {}

            })
    }

}