package cn.edu.gdou.jakec.campusbooking

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cn.edu.gdou.jakec.campusbooking.MyApplication.Companion.context
import cn.edu.gdou.jakec.campusbooking.utility.readBytes
import cn.leancloud.*
import cn.leancloud.types.AVGeoPoint
import cn.leancloud.types.AVNull
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import timber.log.Timber
import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream

class ManageStudyRoomCreateViewModel : ViewModel() {

    private val _finished = MutableLiveData<Boolean>()
    val finished: LiveData<Boolean> get() = _finished

    private val _error = MutableLiveData<Throwable>()
    val error: LiveData<Throwable> get() = _error

    private lateinit var geo: AVGeoPoint
    private lateinit var uri: Uri
    private lateinit var name: String
    private lateinit var imgUrl: String
    private var xCount = 0
    private var yCount = 0

    fun setUri(uri: Uri) {
        this.uri = uri
        Timber.i("File type: %s", context.contentResolver.getType(uri))
    }

    fun setXCount(xCount: Int) {
        this.xCount = xCount
    }

    fun setYCount(yCount: Int) {
        this.yCount = yCount
    }

    fun setName(name: String) {
        this.name = name
    }

    fun setGeo(geo: AVGeoPoint) {
        this.geo = geo
    }

    fun setError(e: Throwable) {
        _error.value = e
    }

    fun create() {
        if (!this::geo.isInitialized || !this::uri.isInitialized) {
            _error.value = AVException(609, context.getString(R.string.image_geo_null))
        } else {
            uploadImage()
        }
    }

    fun uploadImage() {
        val meta = HashMap<String, Any>()
        meta.put("mime_type", context.contentResolver.getType(uri)!!)
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
    }

    fun fetchImageUrl(fileId: String) {
        val file = AVObject.createWithoutData("_File", fileId)
        file.fetchInBackground().subscribe(object : Observer<AVObject> {
            override fun onSubscribe(d: Disposable) {}
            override fun onNext(t: AVObject) {
                imgUrl = t.getString("url")
                createRoom()
            }

            override fun onError(e: Throwable) {
                _error.value = e
            }

            override fun onComplete() {}
        })
    }

    fun createRoom() {
        val room = AVObject("StudyRoom")
        room.put("name", name)
        room.put("isEnabled", false)
        room.put("count", 0)
        room.put("capacity", 0)
        room.put("isSilent", true)
        room.put("xCount", xCount)
        room.put("yCount", yCount)
        room.put("geo", geo)
        room.put("imgUrl", imgUrl)
        room.saveInBackground().subscribe(object : Observer<AVObject> {
            override fun onSubscribe(d: Disposable) {}
            override fun onNext(t: AVObject) {
                _finished.value = true
            }

            override fun onError(e: Throwable) {
                _error.value = e
            }

            override fun onComplete() {}

        })
    }

}
