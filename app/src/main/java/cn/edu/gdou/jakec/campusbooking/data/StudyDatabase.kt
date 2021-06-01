package cn.edu.gdou.jakec.campusbooking.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [StudyRoom::class, StudySeat::class], version = 4, exportSchema = false)
abstract class StudyDatabase : RoomDatabase() {

    abstract val studyRoomDao: StudyRoomDao
    abstract val studySeatDao: StudySeatDao

    companion object {

        @Volatile
        private var INSTANCE: StudyDatabase? = null

        fun getInstance(context: Context): StudyDatabase {

            synchronized(this) {

                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        StudyDatabase::class.java,
                        "study_database"
                    )
                        .fallbackToDestructiveMigration()
                        .build()

                    INSTANCE = instance
                }

                return instance
            }
        }
    }
}
