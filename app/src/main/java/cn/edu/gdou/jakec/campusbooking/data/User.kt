package cn.edu.gdou.jakec.campusbooking.data

import android.provider.ContactsContract
import cn.leancloud.AVUser

data class User(
    val id: String = AVUser.getCurrentUser().objectId,
    val username: String = AVUser.getCurrentUser().username,
    val phone: String = AVUser.getCurrentUser().mobilePhoneNumber,
    val nickname: String,
    val imgUrl: String,
    val violation: Int,
    val resumeAt: Long,
    val role: Role,
)
