package com.github.cozy06.Discord

import com.github.cozy06.School.SchoolCode
import com.github.cozy06.School.SchoolMeal
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder
import org.json.JSONException
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*
import kotlin.concurrent.timerTask

class Meal {
    fun mealMessage(channel: TextChannel) {
        val timer = Timer()
        timer.scheduleAtFixedRate(timerTask {
            val zoneId = ZoneId.of("Asia/Seoul")
            val now = LocalDateTime.now(zoneId)
            if (now.checkToday()) {
                if (now.hour == 10 && now.minute == 45 && now.second == 0) {
                    try {
                        val schoolCode = SchoolCode.findSchoolCode("마포고등학교")
                        val ATPT_OFCDC_SC_CODE = schoolCode.first
                        val SD_SCHUL_CODE = schoolCode.second

                        val date: LocalDate? = LocalDate.now(zoneId)
                        val schoolMeal = SchoolMeal.getSchoolMeal(
                            ATPT_OFCDC_SC_CODE, SD_SCHUL_CODE,
                            date.toString().replace("-", "")
                        )

                        val embed = EmbedBuilder()
                            .setTitle("<급식>  ${date.toString().replace("-", ".")}")
                            .setDescription(schoolMeal)
                            .build()
                        val msg = MessageCreateBuilder()
                            .addEmbeds(embed)
                            .build()
                        channel.sendMessage(msg).queue()
                        println("<급식> 완료! $date")
                    } catch (_: JSONException) {  }
                }
            }
        }, 0L, 1000L) // 60초마다 체크 (밀리초 단위)
    }

    private fun LocalDateTime.checkToday(): Boolean {
        return when(this.dayOfWeek) {
            DayOfWeek.SATURDAY, DayOfWeek.SUNDAY -> false
            else -> true
        }
    }
}