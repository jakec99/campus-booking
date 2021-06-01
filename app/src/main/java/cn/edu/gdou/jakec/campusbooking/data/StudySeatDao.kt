package cn.edu.gdou.jakec.campusbooking.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface StudySeatDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(seat: StudySeat)

    @Query("DELETE FROM study_seat_table")
    suspend fun clear()

    @Query("SELECT * FROM study_seat_table ORDER BY y_index ASC, x_index ASC")
    suspend fun getAllSeats(): List<StudySeat>

    @Query("SELECT * FROM study_seat_table WHERE id = :seatId LIMIT 1")
    suspend fun getSeat(seatId: String): StudySeat

//    @Query("UPDATE study_room_table SET fav_id = :favId WHERE id = :roomId")
//    suspend fun updateFav(favId: String, roomId: String)

//    @Query("SELECT * FROM study_room_table WHERE count = 2")
//    fun getAllRooms(): LiveData<List<StudyRoom>>

//    @Query("DELETE FROM study_room_table")
//    suspend fun clear()

//    @Query("SELECT * from daily_sleep_quality_table WHERE nightId = :key")
//    suspend fun get(key: Long): SleepNight


//    @Query("SELECT * FROM daily_sleep_quality_table ORDER BY nightId DESC")
//    fun getAllNights(): LiveData<List<SleepNight>>

//    @Query("SELECT * FROM daily_sleep_quality_table ORDER BY nightId DESC LIMIT 1")
//    suspend fun getTonight(): SleepNight?
}
