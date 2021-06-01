package cn.edu.gdou.jakec.campusbooking

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cn.edu.gdou.jakec.campusbooking.data.Role
import cn.edu.gdou.jakec.campusbooking.data.StudyRoom
import cn.edu.gdou.jakec.campusbooking.data.StudySchedule
import cn.edu.gdou.jakec.campusbooking.data.UserRepository
import cn.leancloud.AVObject
import cn.leancloud.AVQuery
import cn.leancloud.AVRole
import cn.leancloud.AVUser
import cn.leancloud.types.AVNull
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import timber.log.Timber

class ManageStudyScheduleViewModel : ViewModel() {

    private val _title = MutableLiveData<String>()
    val title: LiveData<String> get() = _title

    private val _schedules = MutableLiveData<List<StudySchedule>>()
    val schedules: LiveData<List<StudySchedule>> get() = _schedules

    private val _error = MutableLiveData<Throwable>()
    val error: LiveData<Throwable> get() = _error

    private val _role = MutableLiveData<Role>()
    val role: LiveData<Role> get() = _role

    private lateinit var room: StudyRoom

    init {
        _role.value = UserRepository.getAVUser().role
    }

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
                            closem = i.getInt("closem"),
                            isManageable = true
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

    fun updateSchedule(schedule: StudySchedule) {
        lateinit var newSchedule: AVObject
        if ((schedule.id).isEmpty()) {
            newSchedule = AVObject("StudySchedule")
            newSchedule.put("roomId", room.id)
            newSchedule.put("day", schedule.day)
        } else {
            newSchedule = AVObject.createWithoutData("StudySchedule", schedule.id)
        }
        newSchedule.put("openh", schedule.openh)
        newSchedule.put("openm", schedule.openm)
        newSchedule.put("closeh", schedule.closeh)
        newSchedule.put("closem", schedule.closem)
        newSchedule.saveInBackground().subscribe(object : Observer<AVObject> {
            override fun onSubscribe(d: Disposable) {}
            override fun onNext(t: AVObject) {
                getSchedules()
            }

            override fun onError(e: Throwable) {
                _error.value = e
            }

            override fun onComplete() {}
        })
    }

    fun createSchedule(schedule: StudySchedule) {
        val newSchedule = AVObject.createWithoutData("StudySchedule", schedule.id)
        newSchedule.put("openh", schedule.openh)
        newSchedule.put("openm", schedule.openm)
        newSchedule.put("closeh", schedule.closeh)
        newSchedule.put("closem", schedule.closem)
        newSchedule.saveInBackground().subscribe(object : Observer<AVObject> {
            override fun onSubscribe(d: Disposable) {}
            override fun onNext(t: AVObject) {
                getSchedules()
            }

            override fun onError(e: Throwable) {
                _error.value = e
            }

            override fun onComplete() {}
        })
    }

    fun deleteSchedule(schedule: StudySchedule) {
        val newSchedule = AVObject.createWithoutData("StudySchedule", schedule.id)
        newSchedule.deleteInBackground().subscribe(object : Observer<AVNull> {
            override fun onSubscribe(d: Disposable) {}
            override fun onNext(t: AVNull) {
                getSchedules()
            }

            override fun onError(e: Throwable) {
                _error.value = e
            }

            override fun onComplete() {}
        })
    }

}