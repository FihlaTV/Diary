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

import io.github.trubitsyn.diary.task.Task
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