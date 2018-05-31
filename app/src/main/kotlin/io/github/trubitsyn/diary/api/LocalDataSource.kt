package io.github.trubitsyn.diary.api

import io.github.trubitsyn.diary.api.task.Task
import org.joda.time.LocalDate

class LocalDataSource : DataSource {
    override fun getTasks(date: LocalDate): List<Task> {
        return listOf()
    }
}