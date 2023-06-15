package com.github.cozy06.School

import com.github.cozy06.File.Companion.toJson
import com.github.cozy06.HttpLogic
import com.github.cozy06.HttpLogic.Companion.httpGET
import java.net.URL

class SchoolMeal {
    companion object {
        fun getSchoolMeal(school_ATPT_OFCDC_SC_CODE: String, school_SD_SCHUL_CODE: String, date: String): String {
            val SchoolMeal = URL("https://open.neis.go.kr/hub/mealServiceDietInfo")
                .httpGET(
                    HttpLogic.params(
                        "KEY" to "80ab2cf9238b4ba3839ba6264f04480a",
                        "Type" to "json",
                        "plndex" to "1",
                        "pSize" to "30",
                        "ATPT_OFCDC_SC_CODE" to school_ATPT_OFCDC_SC_CODE,
                        "SD_SCHUL_CODE" to school_SD_SCHUL_CODE,
                        "MLSV_YMD" to date
                    )
                )?.toJson()?.getJSONArray("mealServiceDietInfo")?.get(1).toString().toJson().getJSONArray("row").get(0).toString().toJson().getString("DDISH_NM").replace("<br/>", "\n")

            return SchoolMeal
        }
    }
}