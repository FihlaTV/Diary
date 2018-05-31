package io.github.trubitsyn.diary.api

import io.github.trubitsyn.diary.api.task.Task
import org.joda.time.LocalDate

interface DataSource {

    fun getTasks(date: LocalDate): List<Task>
}