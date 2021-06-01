package cn.edu.gdou.jakec.campusbooking

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cn.edu.gdou.jakec.campusbooking.data.StudyRoom
import cn.edu.gdou.jakec.campusbooking.utility.readBytes
import cn.leancloud.*
import cn.leancloud.types.AVGeoPoint
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import timber.log.Timber
import java.util.HashMap

class ManageStudyRoomEditViewModel : ViewModel() {

    private val _finished = MutableLiveData<Boolean>()
    val finished: LiveData<Boolean> get() = _finished

    private val _room = MutableLiveData<StudyRoom>()
    val room: LiveData<StudyRoom> get() = _room

    private val _error = MutableLiveData<Throwable>()
    val error: LiveData<Throwable> get() = _error

    private lateinit var newRoom: StudyRoom
    private lateinit var uri: Uri

    fun setUri(uri: Uri) {
        this.uri = uri
        Timber.i("File type: %s", MyApplication.context.contentResolver.getType(uri))
    }

    fun setGeo(geo: AVGeoPoint) {
        newRoom.latitude = geo.latitude
        newRoom.longitude = geo.longitude
    }

    fun setXCount(xCount: Int) {
        newRoom.xCount = xCount
    }

    fun setYCount(yCount: Int) {
        newRoom.yCount = yCount
    }

    fun setName(name: String) {
        newRoom.name = name
    }

    fun setError(e: Throwable) {
        _error.value = e
    }

    fun getRoom(roomId: String) {
        val query = AVQuery<AVObject>("StudyRoom")

        query.getInBackground(roomId).subscribe(object : Observer<AVObject> {
            override fun onSubscribe(d: Disposable) {}

            override fun onNext(t: AVObject) {
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
                newRoom = room
                _room.value = room
            }

            override fun onError(e: Throwable) {
                _error.value = e
            }

            override fun onComplete() {}

        })
    }

    fun uploadImage() {
        if (this::uri.isInitialized) {
            val meta = HashMap<String, Any>()
            meta.put("mime_type", MyApplication.context.contentResolver.getType(uri)!!)
            val file = AVFile("roomImage", readBytes(uri))
            file.saveInBackground().subscribe(object : Observer<AVFile> {
                override fun onSubscribe(d: Disposable) {}
                override fun onNext(t: AVFile) {
                    val fileId = t.objectId
                    fetchImageUrl(fileId)
                }

                override fun onError(e: Throwable) {
                    _error.value = e
                }

                override fun onComplete() {}
            })
        } else {
            edit()
        }
    }

    fun fetchImageUrl(fileId: String) {
        val file = AVObject.createWithoutData("_File", fileId)
        file.fetchInBackground().subscribe(object : Observer<AVObject> {
            override fun onSubscribe(d: Disposable) {}
            override fun onNext(t: AVObject) {
                newRoom.imgUrl = t.getString("url")
                edit()
            }

            override fun onError(e: Throwable) {
                _error.value = e
            }

            override fun onComplete() {}
        })
    }

    fun edit() {
        val params: MutableMap<String, Any> = HashMap()
        params["roomId"] = newRoom.id
        params["roomName"] = newRoom.name
        params["xCount"] = newRoom.xCount
        params["yCount"] = newRoom.yCount
        params["geo"] = AVGeoPoint(newRoom.latitude, newRoom.longitude)
        params["imgUrl"] = newRoom.imgUrl

        AVCloud.callFunctionInBackground<String>("studyRoomEdit", params)
            .subscribe(object : Observer<String> {

                override fun onSubscribe(d: Disposable) {}

                override fun onNext(t: String) {
                    getRoom(t)
                }

                override fun onError(e: Throwable) {
                    _error.value = e
                }

                override fun onComplete() {}
            })

    }

    fun delete() {
//        TODO
    }


}