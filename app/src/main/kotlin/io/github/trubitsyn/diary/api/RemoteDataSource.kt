package io.github.trubitsyn.diary.api

import android.content.Context
import io.github.trubitsyn.diary.DiaryApplication
import io.github.trubitsyn.diary.api.task.Task
import org.joda.time.LocalDate

class RemoteDataSource(context: Context) : DataSource {
    private val networkClient = DiaryApplication.getInstance(context).networkClient

    override fun getTasks(date: LocalDate): List<Task> {
        val document = networkClient.getPageForDate(date)
        val tasks = document!!.select("body > table > tbody > tr > td:nth-child(2) > table > tbody > tr:nth-child(3) > td:nth-child(2) > table > tbody > tr > td > table:nth-child(5) > tbody > tr")

        if (tasks.size > 0) {
            return (1..tasks.size - 2).map {
                var taskText: String = String()
                tasks[it].select("td")

                val subjectField = tasks[it].child(0).select("b.t16").first()?.ownText() ?: ""

                taskText += tasks[it].child(2).select("p").first().ownText()

                val link = tasks[it].select("a")?.firstOrNull()

                if (link != null && link.hasAttr("href")) {
                    val linkAttr = link.attr("href")
                    val htmlLink = "<a href=\"${networkClient.getAbsoluteUrl(linkAttr)}\">[Файл]</a>"
                    taskText += " $htmlLink"
                }

                Task(subjectField, taskText)
            }
        }
        return listOf()
    }
}