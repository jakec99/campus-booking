package cn.edu.gdou.jakec.campusbooking.data

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "study_seat_table", primaryKeys = ["x_index", "y_index"])
data class StudySeat(
    @ColumnInfo(name = "x_index")
    var xIndex: Int,

    @ColumnInfo(name = "y_index")
    var yIndex: Int,

    @ColumnInfo(name = "id")
    var id: String = "",

    @ColumnInfo(name = "type")
    var type: String = "none",

    @ColumnInfo(name = "name")
    var name: String = "",

    @ColumnInfo(name = "is_enabled")
    var isEnabled: Boolean = false,

    @ColumnInfo(name = "is_occupied")
    var isOccupied: Boolean = false,

    var roomId: String = "",
    var roomName: String = "",

    ) : Parcelable
