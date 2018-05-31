/*
 * Copyright 2016, 2018 Nikola Trubitsyn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.trubitsyn.diary.datasource

import android.content.Context
import io.github.trubitsyn.diary.DiaryApplication
import io.github.trubitsyn.diary.task.Task
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