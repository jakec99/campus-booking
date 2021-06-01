package cn.edu.gdou.jakec.campusbooking

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cn.leancloud.AVUser
import cn.leancloud.types.AVNull
import io.reactivex.Observer
import io.reactivex.disposables.Disposable

class SignupViewModel : ViewModel() {

    private val _error = MutableLiveData<Throwable>()
    val error: LiveData<Throwable> get() = _error

    private val _hasSent = MutableLiveData<Boolean?>()
    val hasSent: LiveData<Boolean?> get() = _hasSent

    private val _hasLogined = MutableLiveData<Boolean?>()
    val hasLogined: LiveData<Boolean?> get() = _hasLogined

    fun sendCode(phone: String) {
        AVUser.requestPasswordResetBySmsCodeInBackground(phone)
            .subscribe(object : Observer<AVNull> {
                override fun onSubscribe(d: Disposable) {}

                override fun onNext(t: AVNull) {
                    _hasSent.value = true
                }

                override fun onError(e: Throwable) {
                    _error.value = e
                }

                override fun onComplete() {}
            })
    }

    fun signupOrLogin(phone: String, code: String) {
        AVUser.signUpOrLoginByMobilePhoneInBackground(phone, code)
            .subscribe(object : Observer<AVUser> {
                override fun onSubscribe(d: Disposable) {}

                override fun onNext(t: AVUser) {
                    _hasLogined.value = true
                }

                override fun onError(e: Throwable) {
                    _error.value = e
                }

                override fun onComplete() {}
            })
    }

}