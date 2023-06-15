package com.github.cozy06.School

import com.github.cozy06.File.Companion.toJson
import com.github.cozy06.HttpLogic
import com.github.cozy06.HttpLogic.Companion.httpGET
import java.net.URL
import java.net.URLEncoder

class SchoolCode {
    companion object {
        fun findSchoolCode(schoolName: String): Pair<String, String> {
            val schoolNameEncode = URLEncoder.encode(schoolName, "UTF-8")
            val school = URL("https://open.neis.go.kr/hub/schoolInfo")
                .httpGET(
                    HttpLogic.params(
                        "SCHUL_NM" to schoolNameEncode,
                        "Type" to "json",
                        "pIndex" to "1",
                        "pSize" to "100",
                        "KEY" to "80ab2cf9238b4ba3839ba6264f04480a"
                    )
                )
            val school_ATPT_OFCDC_SC_CODE =
                school?.toJson()?.getJSONArray("schoolInfo")?.get(1).toString().toJson().getJSONArray("row").get(0)
                    .toString().toJson().getString("ATPT_OFCDC_SC_CODE")
            val school_SD_SCHUL_CODE =
                school?.toJson()?.getJSONArray("schoolInfo")?.get(1).toString().toJson().getJSONArray("row").get(0)
                    .toString().toJson().getString("SD_SCHUL_CODE")

            return Pair(school_ATPT_OFCDC_SC_CODE, school_SD_SCHUL_CODE)
        }
    }
}