package cn.edu.gdou.jakec.campusbooking

import androidx.lifecycle.*
import cn.edu.gdou.jakec.campusbooking.data.StudyRoom
import cn.edu.gdou.jakec.campusbooking.data.StudyRoomDao
import cn.leancloud.AVObject
import cn.leancloud.AVQuery
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import timber.log.Timber

class ManageStudyViewModel(dataSource: StudyRoomDao) : ViewModel() {

    val database = dataSource

    private val _isRefreshing = MutableLiveData<Boolean>()
    val isRefreshing: LiveData<Boolean> get() = _isRefreshing

    val rooms: LiveData<List<StudyRoom>> = database.getAllRooms().asLiveData()

    private val _error = MutableLiveData<Throwable>()
    val error: LiveData<Throwable> get() = _error

    init {
        getAllRooms()
        _isRefreshing.value = true
    }

    fun getAllRooms() {

        val query = AVQuery<AVObject>("StudyRoom")
        query.findInBackground().subscribe(object : Observer<List<AVObject>> {
            override fun onSubscribe(d: Disposable) {}

            override fun onNext(u: List<AVObject>) {
                deleteAllRooms()
                if (u.isNotEmpty()) {
                    for (t in u) {
                        val room = StudyRoom(
                            id = t.objectId,
                            name = t.getString("name"),
                            isEnabled = t.getBoolean("isEnabled"),
                            imgUrl = t.getString("imgUrl"),
                            count = t.getInt("count"),
                            capacity = t.getInt("capacity"),
                            xCount = t.getInt("xCount"),
                            yCount = t.getInt("yCount"),
                            openAt = t.getLong("openAt"),
                            closeAt = t.getLong("closeAt"),
                            rate = t.getDouble("rate"),
                            latitude = t.getAVGeoPoint("geo").latitude,
                            longitude = t.getAVGeoPoint("geo").longitude
                        )
                        insertRoom(room)
                    }
                }
            }

            override fun onError(e: Throwable) {
                _error.value = e
            }

            override fun onComplete() {
                _isRefreshing.value = false
            }
        })
    }

    private fun deleteAllRooms() {
        viewModelScope.launch {
            database.clear()
        }
    }

    private fun insertRoom(room: StudyRoom) {
        viewModelScope.launch {
            database.insert(room)
        }
    }

}