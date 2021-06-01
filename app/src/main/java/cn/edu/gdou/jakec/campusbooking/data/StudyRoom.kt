package cn.edu.gdou.jakec.campusbooking.data

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import cn.leancloud.types.AVGeoPoint
import kotlinx.parcelize.Parcelize
import java.sql.Types.NULL

@Parcelize
@Entity(tableName = "study_room_table")
data class StudyRoom(

    @PrimaryKey
    var id: String,

    @ColumnInfo(name = "name")
    var name: String,

    @ColumnInfo(name = "img_url")
    var imgUrl: String,

    @ColumnInfo(name = "is_enabled")
    var isEnabled: Boolean,

    @ColumnInfo(name = "count")
    var count: Int,

    @ColumnInfo(name = "capacity")
    var capacity: Int,

    @ColumnInfo(name = "x_count")
    var xCount: Int,

    @ColumnInfo(name = "y_count")
    var yCount: Int,

    @ColumnInfo(name = "open_at")
    var openAt: Long,

    @ColumnInfo(name = "close_at")
    var closeAt: Long,

    @ColumnInfo(name = "rate")
    var rate: Double,

    @ColumnInfo(name = "latitude")
    var latitude: Double,

    @ColumnInfo(name = "longitude")
    var longitude: Double,

    @ColumnInfo(name = "fav_id")
    var favId: String = "",

    @ColumnInfo(name = "is_manageabled")
    var isManageable: Boolean = false,

    ) : Parcelable
