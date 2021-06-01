package cn.edu.gdou.jakec.campusbooking.data;

data class StudySchedule(

    var id: String,

    var day: Int,

    var openh: Int,

    var openm: Int,

    var closeh: Int,

    var closem: Int,

    var isManageable: Boolean = false,

    )
