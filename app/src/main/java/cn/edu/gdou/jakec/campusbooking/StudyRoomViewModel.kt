package cn.edu.gdou.jakec.campusbooking

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.edu.gdou.jakec.campusbooking.data.StudyOrder
import cn.edu.gdou.jakec.campusbooking.data.StudyRoom
import cn.edu.gdou.jakec.campusbooking.data.StudySeat
import cn.edu.gdou.jakec.campusbooking.data.StudySeatDao
import cn.leancloud.*
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.launch
import timber.log.Timber


class StudyRoomViewModel(dataSource: StudySeatDao) : ViewModel() {

    private val database = dataSource

    private val _title = MutableLiveData<String>()
    val title: LiveData<String> get() = _title

    private val _seats = MutableLiveData<List<StudySeat>>()
    val seats: LiveData<List<StudySeat>> get() = _seats

    private val _order = MutableLiveData<StudyOrder>()
    val order: LiveData<StudyOrder> get() = _order

    private val _orderId = MutableLiveData<String>()
    val orderId: LiveData<String> get() = _orderId

    private val _error = MutableLiveData<Throwable>()
    val error: LiveData<Throwable> get() = _error

    private lateinit var room: StudyRoom

    private val emptySeatList: MutableList<StudySeat> = mutableListOf()
    private val seatList: MutableList<StudySeat> = mutableListOf()
    private var seatsInitialized = false

    fun setRoom(room: StudyRoom) {
        this.room = room
        _title.value = room.name
    }

    fun getRoom(): StudyRoom = room

    fun refreshRoom() {
        val query = AVQuery<AVObject>("StudyRoom")
        query.getInBackground(room.id).subscribe(object : Observer<AVObject> {
            override fun onSubscribe(d: Disposable) {}
            override fun onNext(t: AVObject) {
                room = StudyRoom(
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
                refreshSeats()
            }

            override fun onError(e: Throwable) {
                _error.value = e
            }

            override fun onComplete() {}
        })
    }

    fun refreshSeats() {
        if (!seatsInitialized) {
            for (i in 1..room.xCount) {
                for (j in 1..room.yCount) {
                    emptySeatList.add(StudySeat(i, j))
                }
            }
        }
        val query = AVQuery<AVObject>("StudySeat")
        query.whereEqualTo("roomId", room.id)
        query.findInBackground().subscribe(object : Observer<List<AVObject>> {
            override fun onSubscribe(d: Disposable) {}
            override fun onNext(t: List<AVObject>) {
                for (newSeat in t) {
                    val seat = StudySeat(
                        xIndex = newSeat.getInt("xIndex"),
                        yIndex = newSeat.getInt("yIndex"),
                        id = newSeat.objectId,
                        type = newSeat.getString("type"),
                        name = newSeat.getString("name"),
                        isEnabled = newSeat.getBoolean("isEnabled"),
                        isOccupied = newSeat.getBoolean("isOccupied")
                    )
                    seatList.add(seat)
                }
            }

            override fun onError(e: Throwable) {
                _error.value = e
            }

            override fun onComplete() {
                refreshSeatDatabase()
            }
        })
    }

    fun refreshSeatDatabase() {
        viewModelScope.launch {
            database.clear()
            for (seat in emptySeatList) database.insert(seat)
            for (seat in seatList) database.insert(seat)
            _seats.value = database.getAllSeats()
        }
    }

    fun createOrder(seatId: String) {

        val userId = AVUser.getCurrentUser().objectId

        val params: MutableMap<String, Any> = HashMap()
        params["userId"] = userId as String
        params["seatId"] = seatId

        AVCloud.callFunctionInBackground<String>("studyOrderCreate", params)
            .subscribe(object : Observer<String> {

                override fun onSubscribe(d: Disposable) {}

                override fun onNext(t: String) {
                    _orderId.value = t
                }

                override fun onError(e: Throwable) {
                    _error.value = e
                }

                override fun onComplete() {}
            })
    }

}