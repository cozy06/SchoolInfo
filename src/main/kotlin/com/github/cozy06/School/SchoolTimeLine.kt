package com.github.cozy06

import com.github.cozy06.File.Companion.toJson
import com.github.cozy06.HttpLogic.Companion.httpGET
import java.net.URL

class SchoolTimeLine {
    companion object {
        fun getSchoolTimeLine(school_ATPT_OFCDC_SC_CODE: String, school_SD_SCHUL_CODE: String, date: String): MutableList<String> {
            val schoolTimeLine = URL("https://open.neis.go.kr/hub/hisTimetable")
                .httpGET(
                    HttpLogic.params(
                        "KEY" to "80ab2cf9238b4ba3839ba6264f04480a",
                        "Type" to "json",
                        "plndex" to "1",
                        "pSize" to "20",
                        "ATPT_OFCDC_SC_CODE" to school_ATPT_OFCDC_SC_CODE,
                        "SD_SCHUL_CODE" to school_SD_SCHUL_CODE,
                        "ALL_TI_YMD" to date,
                        "GRADE" to "2",
                        "CLASS_NM" to "02"
                    )
                )
            val schoolTimeLineMax = schoolTimeLine?.toJson()?.getJSONArray("hisTimetable")?.get(0).toString().toJson().getJSONArray("head").get(0).toString().toJson().getInt("list_total_count")

            var schoolTimeLineList: MutableList<String> = mutableListOf()
            repeat(schoolTimeLineMax) {
                schoolTimeLineList.add(schoolTimeLine?.toJson()?.getJSONArray("hisTimetable")?.get(1).toString().toJson().getJSONArray("row").get(it).toString().toJson().getString("ITRT_CNTNT"))
            }
            return schoolTimeLineList
        }
    }
}