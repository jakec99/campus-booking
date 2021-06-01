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

class ManageStudyRoomCheckViewModel : ViewModel() {

    private val _userImgUrl = MutableLiveData<String>()
    val userImgUrl: LiveData<String> get() = _userImgUrl

    private val _nickname = MutableLiveData<String>()
    val nickname: LiveData<String> get() = _nickname

    private val _imgUrl = MutableLiveData<String>()
    val imgUrl: LiveData<String> get() = _imgUrl

    private val _name = MutableLiveData<String>()
    val name: LiveData<String> get() = _name

    private val _columns = MutableLiveData<String>()
    val columns: LiveData<String> get() = _columns

    private val _rows = MutableLiveData<String>()
    val rows: LiveData<String> get() = _rows

    private val _geo = MutableLiveData<String>()
    val geo: LiveData<String> get() = _geo

    private val _finish = MutableLiveData<Boolean>()
    val finish: LiveData<Boolean> get() = _finish

    private val _error = MutableLiveData<Throwable>()
    val error: LiveData<Throwable> get() = _error

    private lateinit var applicationId: String

    fun setTodo(todo: ManageTodo) {
        applicationId = todo.id
        _imgUrl.value = todo.imgUrl
        _nickname.value = todo.nickname
        val query = AVQuery<AVObject>("StudyRoomApplication")
        query.getInBackground(todo.id).subscribe(object : Observer<AVObject> {
            override fun onSubscribe(d: Disposable) {}

            override fun onNext(t: AVObject) {
                _userImgUrl.value = t.getString("userImgUrl")
                _nickname.value = t.getString("nickname")
                _imgUrl.value = t.getString("imgUrl")
                _name.value = t.getString("name")
                _columns.value = t.getInt("xCount").toString()
                _rows.value = t.getInt("yCount").toString()

                val geo =
                    t.getAVGeoPoint("geo").latitude.toString() + ", " + t.getAVGeoPoint("geo").longitude.toString()
                _geo.value = geo
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
        AVCloud.callFunctionInBackground<String>("manageStudyRoomApprove", params)
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