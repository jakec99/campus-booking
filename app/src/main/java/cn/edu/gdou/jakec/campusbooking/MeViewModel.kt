package cn.edu.gdou.jakec.campusbooking

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import cn.leancloud.AVObject
import cn.leancloud.AVQuery
import cn.leancloud.AVUser
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import timber.log.Timber
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

class MeViewModel : ViewModel() {


    init {
//    test()
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun testa() {

        val schedule = arrayListOf<List<Long>>()
        val openTime = LocalDateTime.of(2021, 5, 2, 8, 0)
            .atZone(ZoneId.of("Asia/Shanghai")).toInstant().epochSecond
        val closeTime = LocalDateTime.of(2021, 5, 2, 22, 0)
            .atZone(ZoneId.of("Asia/Shanghai")).toInstant().epochSecond


        val date1 = listOf(
            LocalDateTime.of(2021, 5, 1, 8, 0)
                .atZone(ZoneId.of("Asia/Shanghai")).toInstant().toEpochMilli(),
            LocalDateTime.of(2021, 5, 1, 22, 0)
                .atZone(ZoneId.of("Asia/Shanghai")).toInstant().toEpochMilli())
        val date2 = listOf(
            LocalDateTime.of(2021, 5, 2, 8, 0)
                .atZone(ZoneId.of("Asia/Shanghai")).toInstant().toEpochMilli(),
            LocalDateTime.of(2021, 5, 2, 22, 0)
                .atZone(ZoneId.of("Asia/Shanghai")).toInstant().toEpochMilli())
        val date3 = listOf(
            LocalDateTime.of(2021, 5, 3, 8, 0)
                .atZone(ZoneId.of("Asia/Shanghai")).toInstant().toEpochMilli(),
            LocalDateTime.of(2021, 5, 3, 22, 0)
                .atZone(ZoneId.of("Asia/Shanghai")).toInstant().toEpochMilli())
        val date4 = listOf(
            LocalDateTime.of(2021, 5, 4, 8, 0)
                .atZone(ZoneId.of("Asia/Shanghai")).toInstant().toEpochMilli(),
            LocalDateTime.of(2021, 5, 4, 22, 0)
                .atZone(ZoneId.of("Asia/Shanghai")).toInstant().toEpochMilli())

//        Timber.i("date2: %s", date2.toString())

        schedule.add(date1)
        schedule.add(date2)
        schedule.add(date3)
        schedule.add(date4)

        val todo = AVObject.createWithoutData("StudyRoom", "607ce00b1c179652b668e0bc")
        todo.put("schedule", schedule)
//        todo.put("count", 21)

        Timber.i("start")

        todo.saveInBackground().subscribe(object : Observer<AVObject> {
            override fun onSubscribe(d: Disposable) {}

            override fun onNext(t: AVObject) {
                Timber.i("done")
            }

            override fun onError(e: Throwable) {
                Timber.i("error: %s", e.toString())
            }

            override fun onComplete() {}
        })
    }

    fun testb() {
        val query = AVQuery<AVObject>("StudyRoom")
        query.selectKeys(listOf("schedule"))
        query.findInBackground().subscribe(object : Observer<List<AVObject>> {
            override fun onSubscribe(d: Disposable) {}

            override fun onNext(t: List<AVObject>) {
                for (room in t) {
                    val schedule = room.getList("schedule") as? List<List<Long>>
                    if (schedule != null) {
                        Timber.i(schedule.toString())
                        val now = Calendar.getInstance().timeInMillis
                        Timber.i(now.toString())
                        if (schedule[1][1] > now) {
                            Timber.i("Got what you want season 1")
                        }
                        if (schedule[0][1] < now) {
                            Timber.i("Got what you want season 2")
                            val newSchedule = schedule.drop(1)
                            Timber.i(newSchedule.toString())
                            val todo = AVObject.createWithoutData("StudyRoom", room.objectId)
                            todo.put("schedule", newSchedule)
                        }
                    }
                    if (schedule == null) {
                        Timber.i("the schedule of %s is null", room.objectId)
                    }
                }
            }

            override fun onError(e: Throwable) {
            }

            override fun onComplete() {}
        })
    }

    fun testc() {


        Timber.i("you know what")

        val query = AVQuery<AVObject>("StudyRoom")
//        val room = AVObject.createWithoutData("StudyRoom", "608f734333ced0068b15e732")
        query.whereEqualTo("objectId", "607ce00b1c179652b668e0bc")
        query.firstInBackground.subscribe(object : Observer<AVObject> {
            override fun onSubscribe(d: Disposable) {
                Timber.i("onSubscribe")
            }

            override fun onNext(t: AVObject) {


                Timber.i(t.getString("name"))
                val schedule = t.getList("schedule")
                if (schedule != null) {
                    Timber.i("not null")
                } else {
                    Timber.i("is null")
                }
//                testd(room.objectId)

            }

            override fun onError(e: Throwable) {
                Timber.i("what if %s", e.toString())
            }

            override fun onComplete() {
                Timber.i("onComplete")
            }
        })

    }

    fun testd() {
        val seat = AVObject.createWithoutData("StudySeat", "607ce4c96c08fe13d25ecf98")
        seat.put("roomId", "")
        seat.saveInBackground().subscribe(object : Observer<AVObject> {
            override fun onSubscribe(d: Disposable) {
                Timber.i("onSubscribe")
            }

            override fun onNext(t: AVObject) {
                Timber.i("onNext")
            }

            override fun onError(e: Throwable) {
                Timber.i("onError, %s", e.toString())
            }

            override fun onComplete() {
                Timber.i("onComplete")
            }

        })
    }


    fun logout() {
        AVUser.logOut()
    }

}