package cn.edu.gdou.jakec.campusbooking

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cn.edu.gdou.jakec.campusbooking.data.Role
import cn.edu.gdou.jakec.campusbooking.data.User
import cn.edu.gdou.jakec.campusbooking.data.UserRepository
import cn.leancloud.AVObject
import cn.leancloud.AVQuery
import cn.leancloud.AVRole
import cn.leancloud.AVUser
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import timber.log.Timber
import java.util.concurrent.ArrayBlockingQueue

class ProfileViewModel : ViewModel() {

    private val _user = MutableLiveData<User>()
    val user: LiveData<User> get() = _user

    private lateinit var role: Role

    init {
        _user.value = UserRepository.getAVUser()
    }

    fun logout() {
        UserRepository.logout()
    }

}