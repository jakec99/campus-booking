package cn.edu.gdou.jakec.campusbooking.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class StudyOrderWorker(appContext: Context, params: WorkerParameters) :
    CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        return Result.success()
    }

}