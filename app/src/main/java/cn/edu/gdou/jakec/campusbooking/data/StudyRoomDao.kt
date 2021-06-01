package cn.edu.gdou.jakec.campusbooking.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface StudyRoomDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(room: StudyRoom)

    @Query("UPDATE study_room_table SET fav_id = :favId WHERE id = :roomId")
    suspend fun updateFav(favId: String, roomId: String)

    @Query("UPDATE study_room_table SET is_manageabled = 1 WHERE id = :roomId")
    suspend fun updateManage(roomId: String)

    @Query("UPDATE study_room_table SET is_manageabled = 1")
    suspend fun updateAdmin()

    @Query("SELECT * FROM study_room_table ORDER BY is_manageabled DESC, is_enabled DESC, fav_id DESC")
    fun getAllRooms(): Flow<List<StudyRoom>>

    @Query("SELECT * FROM study_room_table WHERE instr(name, :key)")
    fun getRooms(key: String): Flow<List<StudyRoom>>

    @Query("SELECT * FROM study_room_table WHERE id = :roomId LIMIT 1")
    suspend fun getRoom(roomId: String): StudyRoom

    @Query("SELECT * FROM study_room_table")
    suspend fun getRoomsInList(): List<StudyRoom>

    @Query("DELETE FROM study_room_table")
    suspend fun clear()

}
