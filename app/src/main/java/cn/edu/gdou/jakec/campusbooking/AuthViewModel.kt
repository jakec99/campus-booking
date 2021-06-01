package cn.edu.gdou.jakec.campusbooking

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cn.edu.gdou.jakec.campusbooking.MyApplication
import cn.edu.gdou.jakec.campusbooking.data.Role
import cn.edu.gdou.jakec.campusbooking.data.User
import cn.edu.gdou.jakec.campusbooking.data.UserRepository
import cn.leancloud.*
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import timber.log.Timber


class AuthViewModel : ViewModel() {

    private val _isLoggedIn = MutableLiveData<Boolean>()
    val isLoggedIn: LiveData<Boolean>
        get() = _isLoggedIn

    init {
        initialize()
        getUser()
    }

    private fun initialize() {
        AVOSCloud.initializeSecurely(
            MyApplication.context,
            "y1Dk1SpyriHQBXtNCnaGjMsx-MdYXbMMI",
            "https://y1dk1spy.api.lncldglobal.com"
        )
//        AVOSCloud.initialize(MyApplication.context, "y1Dk1SpyriHQBXtNCnaGjMsx-MdYXbMMI", "sOTIKdvDhWIgz6DEgWGtN5Oo")
    }

    private fun getUser() {
        val user: AVUser? = AVUser.getCurrentUser()
        if (user != null) {

            val userId = user.objectId
            val query = AVQuery<AVObject>("UserProfile")
            query.whereEqualTo("userId", userId)
            query.firstInBackground.subscribe(object : Observer<AVObject> {
                override fun onSubscribe(d: Disposable) {}
                override fun onNext(u: AVObject) {

                    UserRepository.setUser(User(
                        nickname = u.getString("nickname"),
                        imgUrl = u.getString("imgUrl"),
                        violation = u.getInt("violation"),
                        resumeAt = u.getLong("resumeAt"),
                        role = when (u.getString("role")) {
                            "Administrator" -> Role.ADMINISTRATOR
                            "Manager" -> Role.MANAGER
                            else -> Role.USER
                        }
                    ))

                    _isLoggedIn.value = true

                }

                override fun onError(e: Throwable) {}
                override fun onComplete() {}
            })

        } else {

            _isLoggedIn.value = false

        }
    }

}