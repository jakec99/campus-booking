package cn.edu.gdou.jakec.campusbooking

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import cn.edu.gdou.jakec.campusbooking.data.StudyRoomDao

class ManageStudyViewModelFactory(
    private val dataSource: StudyRoomDao,
    private val application: Application,
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ManageStudyViewModel::class.java)) {
            return ManageStudyViewModel(dataSource) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}