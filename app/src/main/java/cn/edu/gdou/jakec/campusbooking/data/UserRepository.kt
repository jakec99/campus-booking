package cn.edu.gdou.jakec.campusbooking.data

import cn.edu.gdou.jakec.campusbooking.MyApplication
import cn.leancloud.AVOSCloud
import cn.leancloud.AVObject
import cn.leancloud.AVQuery
import cn.leancloud.AVUser
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import timber.log.Timber


object UserRepository {

    private lateinit var user: User

    fun logout() {
        AVUser.logOut();
    }


    fun setUser(user: User) {
        this.user = user
    }

    fun getAVUser(): User = user

    fun getAVRole(): Role = user.role


}