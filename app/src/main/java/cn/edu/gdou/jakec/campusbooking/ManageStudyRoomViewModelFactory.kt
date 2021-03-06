package cn.edu.gdou.jakec.campusbooking

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import cn.edu.gdou.jakec.campusbooking.data.StudySeatDao

class ManageStudyRoomViewModelFactory(
    private val dataSource: StudySeatDao,
    private val application: Application,
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ManageStudyRoomViewModel::class.java)) {
            return ManageStudyRoomViewModel(dataSource) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}