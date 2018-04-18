/*
 * Copyright 2016 Nikola Trubitsyn
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

package io.github.trubitsyn.diary.formatter

import io.github.trubitsyn.diary.api.task.Task
import org.junit.Assert

import org.junit.Test

class SimpleTaskFormatterTest {

    @Test
    fun shouldReturnTaskPropertiesWithTwoNewLines() {
        val formatter = SimpleTaskFormatter()
        val task = Task("subject", "task")
        val expected = "${task.subject}: ${task.task}\n\n"
        val actual = formatter.format(task)

        Assert.assertEquals(expected, actual)
    }
}
