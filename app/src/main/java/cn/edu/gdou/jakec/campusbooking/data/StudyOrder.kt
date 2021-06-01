package cn.edu.gdou.jakec.campusbooking.data

import android.os.Parcelable
import cn.edu.gdou.jakec.campusbooking.utility.toText
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
data class StudyOrder(

    val orderId: String,
    val userId: String,
    val userName: String,
    val roomId: String,
    val roomName: String,
    val seatId: String,
    val seatName: String,
    val isSitting: Boolean,
    val expireAt: Long,
    val updatedAt: Long,

    val expireDate: Date = Date(expireAt * 1000),
    val expireTime: String = expireDate.toText(),

    val updateDate: Date = Date(updatedAt * 1000),
    val updateTime: String = updateDate.toText(),

    ) : Parcelable