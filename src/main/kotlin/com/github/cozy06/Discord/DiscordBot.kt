package com.github.cozy06.Discord

import com.github.cozy06.File.Companion.addLine
import com.github.cozy06.File.Companion.readFile
import com.github.cozy06.File.Companion.writeAll
import com.github.cozy06.Logic
import com.github.cozy06.School.SchoolCode
import com.github.cozy06.School.SchoolMeal
import com.github.cozy06.School.SchoolTimeLine
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.events.session.ReadyEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder
import org.json.JSONException
import java.io.File
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

class DiscordBot {
    fun botOn() {
        val token = "YOUR_BOT_CODE"

        val jda = JDABuilder.createDefault(token)
            .enableIntents(GatewayIntent.GUILD_MESSAGES, GatewayIntent.DIRECT_MESSAGES, GatewayIntent.GUILD_MEMBERS, GatewayIntent.MESSAGE_CONTENT)
            .build()

        val commandLetter = "!"


        jda.addEventListener(object : ListenerAdapter() {
            override fun onReady(event: ReadyEvent) {
                val guild = event.jda.getGuildById("1109851816243507202")!!
                val mealChannel = guild.getTextChannelById("1109851911496159332")!!
                val timeLineChannel = guild.getTextChannelById("1109851816889438291")!!

                println("[${jda.selfUser.toString().split(":")[1].split("(")[0]}] is ready!")

                try {
                    Meal().mealMessage(mealChannel)
                } catch (_: JSONException) {  }
                try {
                    TimeLine().TimeLineMessage(timeLineChannel)
                } catch (_: JSONException) {  }
            }

            override fun onMessageReceived(event: MessageReceivedEvent) {
                val playerMessage: Message = event.message
                val content: String = playerMessage.contentRaw
                val channel = event.channel

                val guild = event.jda.getGuildById("1109851816243507202")!!
                val mealChannel = guild.getTextChannelById("1109851911496159332")!!
                val timeLineChannel = guild.getTextChannelById("1109851816889438291")!!

                if(!content.startsWith(commandLetter)) return

                val command = content.split(" ")[0].substring(1)
                val parameters = content.split(" ").toMutableList()
                parameters.removeAt(0)

                when(command) {
                    "schedule", "일정" -> {
                        if(parameters.size < 3 && parameters[0] != "read" && parameters[0] != "읽기" && parameters[0] != "리스트") {
                            channel.sendMessage("Enter please").queue()
                            return
                        }
                        val date = parameters[1]
                        var schedule = ""
                        if(parameters.size >= 3) {
                            repeat(parameters.size - 2) { schedule += "${parameters[it + 2]} " }
                        }

                        when(parameters[0]) {
                            "add", "추가" -> {
                                if(!File("${System.getProperty("user.dir")}/schedule/").exists()) {
                                    File("${System.getProperty("user.dir")}/schedule/").mkdir()
                                }
                                if(!File("${System.getProperty("user.dir")}/schedule/$date.txt").exists()) {
                                    File("${System.getProperty("user.dir")}/schedule/$date.txt")
                                        .writeAll(schedule)
                                }
                                else {
                                    File("${System.getProperty("user.dir")}/schedule/$date.txt")
                                        .addLine(schedule)
                                }
                                playerMessage.reply("schedule added [$date]!").complete()

                                val zoneId = ZoneId.of("Asia/Seoul")
                                if(date == LocalDate.now(zoneId).toString()) {
                                    val schoolCode = SchoolCode.findSchoolCode("마포고등학교")
                                    val ATPT_OFCDC_SC_CODE = schoolCode.first
                                    val SD_SCHUL_CODE = schoolCode.second

                                    val schoolTimeLineList = SchoolTimeLine.getSchoolTimeLine(
                                        ATPT_OFCDC_SC_CODE,
                                        SD_SCHUL_CODE,
                                        date.replace("-", "")
                                    )
                                    var schoolTimeLine = ""
                                    Logic.loop({
                                        schoolTimeLine = "$schoolTimeLine[${it + 1}교시]  ${schoolTimeLineList[it]}\n"
                                    }, schoolTimeLineList.size)

                                    var schedule = "없음"
                                    if(File("${System.getProperty("user.dir")}/schedule/$date.txt").exists()) {
                                        schedule = File("${System.getProperty("user.dir")}/schedule/$date.txt").readFile()
                                    }

                                    schoolTimeLine = "$schoolTimeLine\n\n[일정]\n$schedule"

                                    val embed = EmbedBuilder()
                                        .setTitle("<시간표>  ${date.replace("-", ".")}")
                                        .setDescription(schoolTimeLine)
                                        .build()
                                    val msg = MessageCreateBuilder()
                                        .addEmbeds(embed)
                                        .build()
                                    timeLineChannel.sendMessage(msg).queue()
                                }
                            }

                            "edit", "수정" -> {
                                if(!File("${System.getProperty("user.dir")}/schedule/").exists()) {
                                    File("${System.getProperty("user.dir")}/schedule/").mkdir()
                                }
                                File("${System.getProperty("user.dir")}/schedule/$date.txt")
                                    .writeAll(schedule)
                                playerMessage.reply("schedule edited [$date]!").complete()

                                val zoneId = ZoneId.of("Asia/Seoul")
                                if(date == LocalDate.now(zoneId).toString()) {
                                    val schoolCode = SchoolCode.findSchoolCode("마포고등학교")
                                    val ATPT_OFCDC_SC_CODE = schoolCode.first
                                    val SD_SCHUL_CODE = schoolCode.second

                                    val schoolTimeLineList = SchoolTimeLine.getSchoolTimeLine(
                                        ATPT_OFCDC_SC_CODE,
                                        SD_SCHUL_CODE,
                                        date.replace("-", "")
                                    )
                                    var schoolTimeLine = ""
                                    Logic.loop({
                                        schoolTimeLine = "$schoolTimeLine[${it + 1}교시]  ${schoolTimeLineList[it]}\n"
                                    }, schoolTimeLineList.size)

                                    var schedule = "없음"
                                    if(File("${System.getProperty("user.dir")}/schedule/$date.txt").exists()) {
                                        schedule = File("${System.getProperty("user.dir")}/schedule/$date.txt").readFile()
                                    }

                                    schoolTimeLine = "$schoolTimeLine\n\n[일정]\n$schedule"

                                    val embed = EmbedBuilder()
                                        .setTitle("<시간표>  ${date.replace("-", ".")}")
                                        .setDescription(schoolTimeLine)
                                        .build()
                                    val msg = MessageCreateBuilder()
                                        .addEmbeds(embed)
                                        .build()
                                    timeLineChannel.sendMessage(msg).queue()
                                }
                            }

                            "read", "읽기" -> {
                                if(File("${System.getProperty("user.dir")}/schedule/$date.txt").exists()) {
                                    val schedule = File("${System.getProperty("user.dir")}/schedule/$date.txt").readFile()
                                    playerMessage.reply("[$date]\n\n$schedule").complete()
                                }
                                else {
                                    playerMessage.reply("no schedule [$date]").complete()
                                }
                            }

                            "list", "리스트" -> {
                                File("${System.getProperty("user.dir")}/schedule").walk().forEach {
                                    var schedule = "없음"
                                    if (it.isFile) {
                                        val fileDate = it.toString().split("/").last().split(".")[0]

                                        if(schedule == "없음") {
                                            schedule = "${fileDate.replace("-", ".")}\n${File("${System.getProperty("user.dir")}/schedule/$fileDate.txt").readFile()}"
                                        }
                                        else{
                                            schedule = "$schedule\n${fileDate.replace("-", ".")}\n${File("${System.getProperty("user.dir")}/schedule/$fileDate.txt").readFile()}"
                                        }
                                        playerMessage.reply(schedule).complete()
                                    }
                                }
                            }
                        }
                    }
                    "timeline", "시간표" -> {
                        val zoneId = ZoneId.of("Asia/Seoul")
                        val date = when(parameters.size > 0) {
                            true -> parameters[0]
                            false -> LocalDate.now(zoneId).toString()
                        }

                        val schoolCode = SchoolCode.findSchoolCode("마포고등학교")
                        val ATPT_OFCDC_SC_CODE = schoolCode.first
                        val SD_SCHUL_CODE = schoolCode.second

                        val schoolTimeLineList = SchoolTimeLine.getSchoolTimeLine(
                            ATPT_OFCDC_SC_CODE,
                            SD_SCHUL_CODE,
                            date.replace("-", "")
                        )
                        var schoolTimeLine = ""
                        Logic.loop({
                            schoolTimeLine = "$schoolTimeLine[${it + 1}교시]  ${schoolTimeLineList[it]}\n"
                        }, schoolTimeLineList.size)

                        var schedule = "없음"
                        if(File("${System.getProperty("user.dir")}/schedule/$date.txt").exists()) {
                            schedule = File("${System.getProperty("user.dir")}/schedule/$date.txt").readFile()
                        }

                        schoolTimeLine = "$schoolTimeLine\n\n[일정]\n$schedule"

                        val embed = EmbedBuilder()
                            .setTitle("<시간표>  ${date.replace("-", ".")}")
                            .setDescription(schoolTimeLine)
                            .build()
                        val msg = MessageCreateBuilder()
                            .addEmbeds(embed)
                            .build()
                        timeLineChannel.sendMessage(msg).queue()
                        playerMessage.reply("OK!").complete()
                    }

                    "meal", "급식" -> {
                        val zoneId = ZoneId.of("Asia/Seoul")
                        val date = when(parameters.size > 0) {
                            true -> parameters[0]
                            false -> LocalDate.now(zoneId).toString()
                        }

                        val schoolCode = SchoolCode.findSchoolCode("마포고등학교")
                        val ATPT_OFCDC_SC_CODE = schoolCode.first
                        val SD_SCHUL_CODE = schoolCode.second

                        val schoolMeal = SchoolMeal.getSchoolMeal(
                            ATPT_OFCDC_SC_CODE, SD_SCHUL_CODE,
                            date.replace("-", "")
                        )

                        val embed = EmbedBuilder()
                            .setTitle("<급식>  ${date.replace("-", ".")}")
                            .setDescription(schoolMeal)
                            .build()
                        val msg = MessageCreateBuilder()
                            .addEmbeds(embed)
                            .build()
                        mealChannel.sendMessage(msg).queue()
                        playerMessage.reply("OK!").complete()
                    }
                }
            }
        })
    }
}