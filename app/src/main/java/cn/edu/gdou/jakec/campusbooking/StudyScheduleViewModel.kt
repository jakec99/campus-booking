package cn.edu.gdou.jakec.campusbooking

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cn.edu.gdou.jakec.campusbooking.data.StudyRoom
import cn.edu.gdou.jakec.campusbooking.data.StudySchedule
import cn.leancloud.AVObject
import cn.leancloud.AVQuery
import io.reactivex.Observer
import io.reactivex.disposables.Disposable

class StudyScheduleViewModel : ViewModel() {

    private val _title = MutableLiveData<String>()
    val title: LiveData<String> get() = _title

    private val _schedules = MutableLiveData<List<StudySchedule>>()
    val schedules: LiveData<List<StudySchedule>> get() = _schedules

    private val _error = MutableLiveData<Throwable>()
    val error: LiveData<Throwable> get() = _error

    private lateinit var room: StudyRoom

    fun setRoom(room: StudyRoom) {
        this.room = room
        _title.value = room.name
    }

    fun getSchedules() {
        val querySchedule = AVQuery<AVObject>("StudySchedule")
        querySchedule.whereEqualTo("roomId", room.id)
        querySchedule.findInBackground().subscribe(object : Observer<List<AVObject>> {
            override fun onSubscribe(d: Disposable) {}
            override fun onNext(t: List<AVObject>) {
                if (t.isNotEmpty()) {
                    val schedules = mutableListOf<StudySchedule>()
                    for (i in t) {
                        val schedule = StudySchedule(
                            id = i.objectId,
                            day = i.getInt("day"),
                            openh = i.getInt("openh"),
                            openm = i.getInt("openm"),
                            closeh = i.getInt("closeh"),
                            closem = i.getInt("closem")
                        )
                        schedules.add(schedule)
                    }
                    _schedules.value = schedules
                }
            }

            override fun onError(e: Throwable) {
                _error.value = e
            }

            override fun onComplete() {}
        })
    }


}