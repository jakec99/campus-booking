package cn.edu.gdou.jakec.campusbooking

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.os.Trace
import io.reactivex.plugins.RxJavaPlugins
import timber.log.Timber


class MyApplication : Application() {

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
    }

    override fun onCreate() {
        super.onCreate()

        context = applicationContext

        Timber.plant(Timber.DebugTree())
    }
}