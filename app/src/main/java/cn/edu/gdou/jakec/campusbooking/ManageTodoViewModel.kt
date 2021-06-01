package cn.edu.gdou.jakec.campusbooking

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cn.edu.gdou.jakec.campusbooking.data.ManageTodo
import cn.edu.gdou.jakec.campusbooking.data.Role
import cn.edu.gdou.jakec.campusbooking.data.Todo
import cn.edu.gdou.jakec.campusbooking.data.User
import cn.leancloud.AVObject
import cn.leancloud.AVQuery
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import timber.log.Timber

class ManageTodoViewModel : ViewModel() {

    private val _error = MutableLiveData<Throwable>()
    val error: LiveData<Throwable> get() = _error

    private val _todos = MutableLiveData<List<ManageTodo>>()
    val todos: LiveData<List<ManageTodo>> get() = _todos

    private val _isRefreshing = MutableLiveData<Boolean>()
    val isRefreshing: LiveData<Boolean> get() = _isRefreshing

    init {
        getTodos()
        _isRefreshing.value = true
    }

    fun getTodos() {
        val queryManager = AVQuery<AVObject>("StudyManagerApplication")
        queryManager.findInBackground().subscribe(object : Observer<List<AVObject>> {
            override fun onSubscribe(d: Disposable) {}

            override fun onNext(applications: List<AVObject>) {
                val todos = mutableListOf<ManageTodo>()

                if (applications.isNotEmpty()) {
                    for (application in applications) {
                        val todo = ManageTodo(
                            type = Todo.MANAGER_APPLICATION,
                            id = application.objectId,
                            nickname = application.getString("nickname"),
                            imgUrl = application.getString("imgUrl")
                        )
                        todos.add(todo)
                    }
                }

                val queryRoom = AVQuery<AVObject>("StudyRoomApplication")
                queryRoom.findInBackground().subscribe(object : Observer<List<AVObject>> {
                    override fun onSubscribe(d: Disposable) {}

                    override fun onNext(rooms: List<AVObject>) {
                        for (room in rooms) {
                            val todo = ManageTodo(
                                type = Todo.STUDY_ROOM_APPLICATION,
                                id = room.objectId,
                                nickname = room.getString("nickname"),
                                imgUrl = room.getString("imgUrl")
                            )
                            todos.add(todo)
                        }

                        _todos.value = todos
                    }

                    override fun onError(e: Throwable) {
                        _error.value = e
                    }

                    override fun onComplete() {
                        _isRefreshing.value = false
                    }

                })
            }

            override fun onError(e: Throwable) {
                _error.value = e
            }

            override fun onComplete() {}
        })

    }

}