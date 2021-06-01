package cn.edu.gdou.jakec.campusbooking.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ManageTodo(
//    0->ManagerApply, 1->RoomApply
    var type: Todo,
    var id: String,
    var nickname: String,
    var imgUrl: String,
    var content: String = when (type) {
        Todo.MANAGER_APPLICATION -> "Applying to be a manager."
        else -> "Applying to create a new room."
    },
) : Parcelable
