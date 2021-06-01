package cn.edu.gdou.jakec.campusbooking

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.edu.gdou.jakec.campusbooking.MyApplication.Companion.context
import cn.edu.gdou.jakec.campusbooking.data.UserRepository
import cn.leancloud.AVObject
import cn.leancloud.AVQuery
import cn.leancloud.types.AVNull
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.launch

class ManageStudyApplyViewModel : ViewModel() {

    private val _error = MutableLiveData<Throwable>()
    val error: LiveData<Throwable> get() = _error

    private val _finish = MutableLiveData<Boolean?>()
    val finish: LiveData<Boolean?> get() = _finish

    fun apply(realname: String, phone: String, address: String, reason: String) {
        var hasApplied = false
        val query = AVQuery<AVObject>("StudyManagerApplication")
        query.whereEqualTo("userId", UserRepository.getAVUser().id)
        query.firstInBackground.subscribe(object : Observer<AVObject> {
            override fun onSubscribe(d: Disposable) {}

            override fun onNext(t: AVObject) {
                hasApplied = true
                _error.value = Throwable(context.getString(R.string.you_have_been_applying))
            }

            override fun onError(e: Throwable) {
                _error.value = e
            }

            override fun onComplete() {
                if (!hasApplied) {
                    val user = UserRepository.getAVUser()
                    val application = AVObject("StudyManagerApplication")
                    application.put("realname", realname)
                    application.put("phone", phone)
                    application.put("address", address)
                    application.put("reason", reason)
                    application.put("userId", user.id)
                    application.put("imgUrl", user.imgUrl)
                    application.put("nickname", user.nickname)
                    application.saveInBackground().subscribe(object : Observer<AVObject> {
                        override fun onSubscribe(d: Disposable) {}

                        override fun onNext(t: AVObject) {
                            _finish.value = true
                        }

                        override fun onError(e: Throwable) {
                            _error.value = e
                        }

                        override fun onComplete() {}

                    })
                }
            }

        })

    }
}