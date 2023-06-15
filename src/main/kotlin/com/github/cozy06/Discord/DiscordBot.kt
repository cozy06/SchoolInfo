package com.github.cozy06

import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel
import net.dv8tion.jda.api.events.session.ReadyEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.requests.GatewayIntent
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*
import kotlin.concurrent.timerTask

class DiscordBot {

    private lateinit var timer: Timer
    val sended = true
    fun botOn() {
        val token = "ODkzNzc1ODAwNjEwOTgzOTU3.GkToEQ.cnuiZ_kkovnnqa2iUVrWL_nPL-7uLBAHe1O-gA"

        val jda = JDABuilder.createDefault(token)
            .enableIntents(GatewayIntent.GUILD_MESSAGES, GatewayIntent.DIRECT_MESSAGES, GatewayIntent.GUILD_MEMBERS, GatewayIntent.MESSAGE_CONTENT)
            .build()

        val commandLetter = "!"



        jda.addEventListener(object : ListenerAdapter() {
            override fun onReady(event: ReadyEvent) {
                val guild = event.jda.getGuildById("846092538670481479")
                val channel = guild?.getTextChannelById("846092574129127445")!!

                println("[${jda.selfUser.toString().split(":")[1].split("(")[0]}] is ready!")

                val schoolCode = SchoolCode.findSchoolCode("마포고등학교")
                val ATPT_OFCDC_SC_CODE = schoolCode.first
                val SD_SCHUL_CODE = schoolCode.second

//                val date: LocalDate? = LocalDate.now()
                val date = "2023-05-19"
                val schoolTimeLine = SchoolTimeLine.getSchoolTimeLine(
                    ATPT_OFCDC_SC_CODE,
                    SD_SCHUL_CODE,
                    date.toString().replace("-", "")
                )
                val schoolMeal =
                    SchoolMeal.getSchoolMeal(ATPT_OFCDC_SC_CODE, SD_SCHUL_CODE, date.toString().replace("-", ""))

                channel.sendMessage("$schoolMeal").queue()


                mealMessage(channel)
                println("send?!")
            }
        })
    }

    fun mealMessage(channel: TextChannel) {
        timer = Timer()
        timer.scheduleAtFixedRate(timerTask {
            val now = LocalDateTime.now()
            if (now.checkToday()) {
                if (now.hour == 10 && now.minute == 30) {
                    channel.sendMessage("1시!").queue()
                }
            }
        }, 0L, 1000L) // 1초마다 체크 (밀리초 단위)
    }

    fun LocalDateTime.checkToday(): Boolean {
        return when(this.dayOfWeek) {
            DayOfWeek.SUNDAY -> false
            else -> true
        }
    }
}