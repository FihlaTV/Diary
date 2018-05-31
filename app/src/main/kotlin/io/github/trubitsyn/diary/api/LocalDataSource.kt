package io.github.trubitsyn.diary.api

import io.github.trubitsyn.diary.api.task.Task
import org.joda.time.LocalDate

class LocalDataSource : DataSource {
    override fun getTasks(date: LocalDate): List<Task> {
        return listOf(
                Task("География", "повторение темы"),
                Task("Биология", "записи в тетради"),
                Task("Химия", "§ 16 в. 1, 2"),
                Task("Математика (алгебра и начала анализа)", "повторить пройденное"),
                Task("Математика (геометрия)", "№ 650"),
                Task("Литература", "вопросы стр. 183-185"),
                Task("Информатика", "конспект")
        )
    }
}