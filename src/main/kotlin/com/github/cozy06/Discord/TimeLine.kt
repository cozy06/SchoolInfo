package com.github.cozy06.Discord

import com.github.cozy06.File.Companion.readFile
import com.github.cozy06.Logic.Companion.loop
import com.github.cozy06.School.SchoolCode
import com.github.cozy06.School.SchoolTimeLine
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder
import org.json.JSONException
import java.io.File
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*
import kotlin.concurrent.timerTask

class TimeLine {
    fun TimeLineMessage(channel: TextChannel) {
        val timer = Timer()
        var timeLine: String? = null
        timer.scheduleAtFixedRate(timerTask {
            val zoneId = ZoneId.of("Asia/Seoul")
            val now = LocalDateTime.now(zoneId)
            try {
                if (now.checkToday()) {
                    if (now.hour == 22 && now.minute == 30 && now.second == 0) {
                        val schoolCode = SchoolCode.findSchoolCode("마포고등학교")
                        val ATPT_OFCDC_SC_CODE = schoolCode.first
                        val SD_SCHUL_CODE = schoolCode.second

                        val tomorrowDate: LocalDate? = LocalDate.now(zoneId).plusDays(1)
                        val schoolTimeLineList = SchoolTimeLine.getSchoolTimeLine(
                            ATPT_OFCDC_SC_CODE,
                            SD_SCHUL_CODE,
                            tomorrowDate.toString().replace("-", "")
                        )
                        var schoolTimeLine = ""
                        loop({
                            schoolTimeLine = "$schoolTimeLine[${it + 1}교시]  ${schoolTimeLineList[it]}\n"
                        }, schoolTimeLineList.size)

                        var schedule = "없음"
                        if(File("${System.getProperty("user.dir")}/schedule/$tomorrowDate.txt").exists()) {
                            schedule = File("${System.getProperty("user.dir")}/schedule/$tomorrowDate.txt").readFile()
                        }

                        schoolTimeLine = "$schoolTimeLine\n\n[일정]\n$schedule"

                        timeLine = schoolTimeLine

                        val embed = EmbedBuilder()
                            .setTitle("<시간표>  ${tomorrowDate.toString().replace("-", ".")}")
                            .setDescription(schoolTimeLine)
                            .build()
                        val msg = MessageCreateBuilder()
                            .addEmbeds(embed)
                            .build()
                        channel.sendMessage(msg).queue()


                        if (tomorrowDate != null) {
                            File("${System.getProperty("user.dir")}/schedule").walk().forEach {
                                if (it.isFile) {
                                    val fileDate = it.toString().split("/").last().split(".")[0]

                                    if(tomorrowDate.toString().replace("-", "").toInt() > fileDate.replace("-", "").toInt()){
                                        File("${System.getProperty("user.dir")}/schedule/$fileDate.txt").delete()
                                    }
                                }
                            }
                            println("<시간표> 완료! ${tomorrowDate.plusDays(1)}")
                        }
                    }
                    else if(now.hour == 8 && now.minute == 0 && now.second == 0) {
                        val schoolCode = SchoolCode.findSchoolCode("마포고등학교")
                        val ATPT_OFCDC_SC_CODE = schoolCode.first
                        val SD_SCHUL_CODE = schoolCode.second

                        val todayDate: LocalDate? = LocalDate.now(zoneId)
                        val schoolTimeLineList = SchoolTimeLine.getSchoolTimeLine(
                            ATPT_OFCDC_SC_CODE,
                            SD_SCHUL_CODE,
                            todayDate.toString().replace("-", "")
                        )
                        var schoolTimeLine = ""
                        loop({
                            schoolTimeLine = "$schoolTimeLine[${it + 1}교시]  ${schoolTimeLineList[it]}\n"
                        }, schoolTimeLineList.size)

                        var schedule = "없음"
                        if(File("${System.getProperty("user.dir")}/schedule/$todayDate.txt").exists()) {
                            schedule = File("${System.getProperty("user.dir")}/schedule/$todayDate.txt").readFile()
                        }

                        schoolTimeLine = "$schoolTimeLine\n\n[일정]\n$schedule"

                        if(timeLine != schoolTimeLine && !timeLine.isNullOrEmpty()) {
                            val embed = EmbedBuilder()
                                .setTitle("<시간표 변경>  ${todayDate.toString().replace("-", ".")}")
                                .setDescription(schoolTimeLine)
                                .build()
                            val msg = MessageCreateBuilder()
                                .addEmbeds(embed)
                                .build()
                            channel.sendMessage(msg).queue()
                        }
                    }
                }
            } catch (_: JSONException) {  }
        }, 0L, 1000L) // 60초마다 체크 (밀리초 단위)
    }

    private fun LocalDateTime.checkToday(): Boolean {
        return when(this.dayOfWeek) {
            DayOfWeek.FRIDAY, DayOfWeek.SATURDAY -> false
            else -> true
        }
    }
}