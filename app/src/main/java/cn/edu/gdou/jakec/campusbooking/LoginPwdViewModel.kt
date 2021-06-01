package cn.edu.gdou.jakec.campusbooking

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cn.leancloud.AVUser
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import timber.log.Timber

class LoginPwdViewModel : ViewModel() {

    private val _loginSucceed = MutableLiveData<Boolean>()
    val loginSucceed: LiveData<Boolean>
        get() = _loginSucceed

    private val _loginError = MutableLiveData<Throwable>()
    val loginError: LiveData<Throwable>
        get() = _loginError

    fun login(username: String, password: String) {
        when {
            Patterns.EMAIL_ADDRESS.matcher(username).matches() -> {
                Timber.i("Login email: %s", username)
                loginByEmail(username, password)
            }
            Patterns.PHONE.matcher(username).matches() -> {
                Timber.i("Login phone: %s", username)
                loginByPhone(username, password)
            }
            else -> {
                Timber.i("Login username: %s", username)
                loginByUsername(username, password)
            }
        }
    }

    private fun loginByUsername(username: String, password: String) {

        AVUser.logIn(username, password).subscribe(object : Observer<AVUser> {
            override fun onSubscribe(d: Disposable) {}
            override fun onNext(t: AVUser) {
                _loginSucceed.value = true
            }

            override fun onError(e: Throwable) {
                Timber.i(e.toString())
                _loginSucceed.value = false
                _loginError.value = e
            }

            override fun onComplete() {}
        })
    }

    private fun loginByEmail(email: String, password: String) {

        AVUser.loginByEmail(email, password).subscribe(object : Observer<AVUser> {
            override fun onSubscribe(d: Disposable) {}
            override fun onNext(t: AVUser) {
                _loginSucceed.value = true
            }

            override fun onError(e: Throwable) {
                Timber.i(e.toString())
                _loginSucceed.value = false
                _loginError.value = e
            }

            override fun onComplete() {}
        })
    }

    private fun loginByPhone(phone: String, password: String) {
        AVUser.loginByMobilePhoneNumber(phone, password)
            .subscribe(object : Observer<AVUser> {
                override fun onSubscribe(d: Disposable) {}
                override fun onNext(t: AVUser) {
                    _loginSucceed.value = true
                }

                override fun onError(e: Throwable) {
                    Timber.i(e.toString())
                    _loginSucceed.value = false
                    _loginError.value = e
                }

                override fun onComplete() {}
            })
    }
}