package cn.edu.gdou.jakec.campusbooking

import android.location.Location
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cn.edu.gdou.jakec.campusbooking.data.UserRepository
import cn.edu.gdou.jakec.campusbooking.utility.readBytes
import cn.leancloud.AVException
import cn.leancloud.AVFile
import cn.leancloud.AVObject
import cn.leancloud.AVQuery
import cn.leancloud.types.AVGeoPoint
import io.reactivex.Observer
import io.reactivex.disposables.Disposable

class
ManageStudyRoomApplyViewModel : ViewModel() {

    private val _error = MutableLiveData<Throwable>()
    val error: LiveData<Throwable> get() = _error

    private val _finish = MutableLiveData<Boolean?>()
    val finish: LiveData<Boolean?> get() = _finish

    private lateinit var uri: Uri
    private lateinit var geo: AVGeoPoint

    fun setUri(uri: Uri) {
        this.uri = uri
    }

    fun setLocation(location: Location) {
        geo = AVGeoPoint(location.latitude, location.longitude)
    }

    fun uploadImage(name: String, xCount: String, yCount: String) {
        if (!this::geo.isInitialized || !this::uri.isInitialized) {
            _error.value =
                AVException(609, MyApplication.context.getString(R.string.image_geo_null))
        } else {
            val meta = HashMap<String, Any>()
            meta.put("mime_type", MyApplication.context.contentResolver.getType(uri)!!)
            val file = AVFile("roomImage", readBytes(uri))
            file.saveInBackground().subscribe(object : Observer<AVFile> {
                override fun onSubscribe(d: Disposable) {}
                override fun onNext(t: AVFile) {
                    val url = t.url
                    apply(name, xCount, yCount, url)
                }

                override fun onError(e: Throwable) {
                    _error.value = e
                }

                override fun onComplete() {}
            })
        }
    }

    fun apply(name: String, xCount: String, yCount: String, url: String) {
        var hasApplied = false
        val query = AVQuery<AVObject>("StudyRoomApplication")
        query.whereEqualTo("userId", UserRepository.getAVUser().id)
        query.firstInBackground.subscribe(object : Observer<AVObject> {
            override fun onSubscribe(d: Disposable) {}

            override fun onNext(t: AVObject) {
                hasApplied = true
                _error.value =
                    Throwable(MyApplication.context.getString(R.string.you_have_been_applying))
            }

            override fun onError(e: Throwable) {
                _error.value = e
            }

            override fun onComplete() {
                if (!hasApplied) {
                    val user = UserRepository.getAVUser()
                    val application = AVObject("StudyRoomApplication")
                    application.put("userId", user.id)
                    application.put("nickname", user.nickname)
                    application.put("name", name)
                    application.put("xCount", xCount)
                    application.put("yCount", yCount)
                    application.put("imgUrl", url)
                    application.put("userImgUrl", user.imgUrl)
                    application.put("geo", geo)
                    application.saveInBackground().subscribe(object : Observer<AVObject> {
                        override fun onSubscribe(d: Disposable) {}

                        override fun onNext(t: AVObject) {
                            _finish.value = true
                        }

                        override fun onError(e: Throwable) {
                            _error.value = e
                        }

                        override fun onComplete() {}

                    })
                }
            }
        })

    }

    fun setError(e: Throwable) {
        _error.value = e
    }
}