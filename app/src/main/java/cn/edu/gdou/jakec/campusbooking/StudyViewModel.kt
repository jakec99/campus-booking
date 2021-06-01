package cn.edu.gdou.jakec.campusbooking

import androidx.lifecycle.*
import cn.edu.gdou.jakec.campusbooking.data.Role
import cn.edu.gdou.jakec.campusbooking.data.StudyRoom
import cn.edu.gdou.jakec.campusbooking.data.StudyRoomDao
import cn.edu.gdou.jakec.campusbooking.data.UserRepository
import cn.leancloud.*
import cn.leancloud.types.AVNull
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.Instant


class StudyViewModel(dataSource: StudyRoomDao) : ViewModel() {

    val database = dataSource

    private val key: MutableStateFlow<String> = MutableStateFlow("")

    val rooms: LiveData<List<StudyRoom>> = key.flatMapLatest { key ->
        if (key == "") {
            database.getAllRooms()
        } else {
            database.getRooms(key)
        }
    }.asLiveData()

    private val _orderId = MutableLiveData<String>()
    val orderId: LiveData<String>
        get() = _orderId

    private val _isRefreshing = MutableLiveData(false)
    val isRefreshing: LiveData<Boolean>
        get() = _isRefreshing

    private val _error = MutableLiveData<String>()
    val error: LiveData<String>
        get() = _error

    private val _role = MutableLiveData<Role>()
    val role: LiveData<Role> get() = _role

    init {
        _isRefreshing.value = true
        _role.value = UserRepository.getAVUser().role
        getAllRooms()
        orderCheck()
    }

    fun setKey(key: String) {
        this.key.value = key
    }

    private fun orderCheck() {
        val now = Instant.now().epochSecond
        val userId = AVUser.getCurrentUser().objectId
        val query = AVQuery<AVObject>("StudyOrder")
        query.whereEqualTo("userId", userId)
        query.whereGreaterThanOrEqualTo("expireAt", now)
        query.firstInBackground.subscribe(object : Observer<AVObject> {
            override fun onSubscribe(d: Disposable) {}
            override fun onNext(t: AVObject) {
                val id = t.objectId
                _orderId.value = id
            }

            override fun onError(e: Throwable) {
                _error.value = e.message

            }

            override fun onComplete() {}
        })
    }

    fun getAllRooms() {
        val query = AVQuery<AVObject>("StudyRoom")
        query.findInBackground().subscribe(object : Observer<List<AVObject>> {
            override fun onSubscribe(d: Disposable) {}

            override fun onNext(u: List<AVObject>) {
                _isRefreshing.value = false
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
                getFavRooms()
            }

            override fun onError(e: Throwable) {
                _isRefreshing.value = false
                _error.value = e.message
            }

            override fun onComplete() {}
        })
    }

    fun getFavRooms() {
        val userId = AVUser.getCurrentUser().objectId
        val query = AVQuery<AVObject>("StudyFavRoom")

        query.whereEqualTo("userId", userId)
        query.findInBackground().subscribe(object : Observer<List<AVObject>> {
            override fun onSubscribe(d: Disposable) {}

            override fun onNext(t: List<AVObject>) {

                if (t.isNotEmpty()) {
                    for (fav in t) {
                        val roomId = fav.getString("roomId")
                        insertFavRoom(fav.objectId, roomId)
                    }
                }

                if (UserRepository.getAVRole() == Role.ADMINISTRATOR) {
                    insertAdminRoom()
                } else if (UserRepository.getAVRole() == Role.MANAGER) {
                    getManageRooms()
                }

            }

            override fun onError(e: Throwable) {
                _error.value = e.message
            }

            override fun onComplete() {}
        })
    }

    fun getManageRooms() {
        val userId = AVUser.getCurrentUser().objectId
        val query = AVQuery<AVObject>("StudyManager")
        query.whereEqualTo("userId", userId)
        query.findInBackground().subscribe(object : Observer<List<AVObject>> {
            override fun onSubscribe(d: Disposable) {}

            override fun onNext(t: List<AVObject>) {

                if (t.isNotEmpty()) {
                    for (fav in t) {
                        val roomId = fav.getString("roomId")
                        insertManageRoom(roomId)
                    }
                }
            }

            override fun onError(e: Throwable) {
                _error.value = e.message
            }

            override fun onComplete() {}
        })
    }

    fun setFavRoom(roomId: String) {
        val fav = AVObject("StudyFavRoom")
        val userId = AVUser.getCurrentUser().objectId
        fav.put("userId", userId)
        fav.put("roomId", roomId)
        fav.saveInBackground().subscribe(object : Observer<AVObject> {
            override fun onSubscribe(d: Disposable) {}

            override fun onNext(item: AVObject) {
                insertFavRoom(item.objectId, roomId)
                orderCheck()
            }

            override fun onError(e: Throwable) {
                _error.value = e.message
            }

            override fun onComplete() {}
        })
    }

    fun cancelFavRoom(roomId: String, favId: String) {
        val fav = AVObject.createWithoutData("StudyFavRoom", favId)
        fav.deleteInBackground().subscribe(object : Observer<AVNull> {
            override fun onSubscribe(d: Disposable) {}

            override fun onNext(t: AVNull) {
                insertFavRoom("", roomId)
            }

            override fun onError(e: Throwable) {
                _error.value = e.message
            }

            override fun onComplete() {}
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

    private fun insertFavRoom(favId: String, roomId: String) {
        viewModelScope.launch {
            database.updateFav(favId, roomId)
        }
    }

    private fun insertAdminRoom() {
        viewModelScope.launch {
            database.updateAdmin()
        }
    }

    private fun insertManageRoom(roomId: String) {
        viewModelScope.launch {
            database.updateManage(roomId)
        }
    }

}