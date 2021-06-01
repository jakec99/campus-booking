package cn.edu.gdou.jakec.campusbooking

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cn.edu.gdou.jakec.campusbooking.data.Message
import cn.edu.gdou.jakec.campusbooking.data.StudySchedule
import cn.edu.gdou.jakec.campusbooking.data.UserRepository
import cn.edu.gdou.jakec.campusbooking.utility.toText
import cn.leancloud.AVObject
import cn.leancloud.AVQuery
import io.reactivex.Observer
import io.reactivex.disposables.Disposable

class MessageViewModel : ViewModel() {

    private val _messages = MutableLiveData<List<Message>>()
    val messages: LiveData<List<Message>> get() = _messages

    private val _isRefreshing = MutableLiveData<Boolean>()
    val isRefreshing: LiveData<Boolean> get() = _isRefreshing

    private val _error = MutableLiveData<Throwable>()
    val error: LiveData<Throwable> get() = _error

    init {
        getMessages()
        _isRefreshing.value = true
    }

    fun getMessages() {
        val queryMessage = AVQuery<AVObject>("UserMessage")
        queryMessage.whereEqualTo("userId", UserRepository.getAVUser().id)
        queryMessage.orderByDescending("createdAt")
        queryMessage.findInBackground().subscribe(object : Observer<List<AVObject>> {
            override fun onSubscribe(d: Disposable) {}

            override fun onNext(t: List<AVObject>) {
                if (t.isNotEmpty()) {
                    val messages = mutableListOf<Message>()
                    for (i in t) {
                        val message = Message(
                            title = i.getString("title"),
                            date = i.getDate("createdAt").toText(),
                            content = i.getString("content")
                        )
                        messages.add(message)
                    }
                    _messages.value = messages
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
}