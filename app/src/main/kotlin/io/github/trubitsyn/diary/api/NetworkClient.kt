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

package io.github.trubitsyn.diary.api

import io.github.trubitsyn.diary.api.auth.Credentials
import org.joda.time.LocalDate
import org.joda.time.format.DateTimeFormat
import org.jsoup.Connection
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import kotlin.properties.Delegates

class NetworkClient {
    var cookies: MutableMap<String, String> by Delegates.notNull()
    val root = "http://localhost/"
    val loginUrl = "login.php"

    fun auth(credentials: Credentials) {
        val response = Jsoup.connect(getAbsoluteUrl(loginUrl))
                .method(Connection.Method.POST)
                .data("login", credentials.login)
                .data("password", credentials.password)
                .execute()

        val requestFailed = response.body().contains(""" http-equiv="Refresh" """)

        if (requestFailed) {
            throw IllegalStateException("Not logged in")
        } else {
            cookies = response.cookies()
        }
    }

    fun getPageForDate(date: LocalDate): Document? {
        val formatter = DateTimeFormat.forPattern("yyyy-MM-dd")
        val plainDate = formatter.print(date)
        return get("index.php?date=" + plainDate)?.parse()
    }

    fun get(url: String): Connection.Response? {
        return Jsoup.connect(getAbsoluteUrl(url))
                .method(Connection.Method.GET)
                .cookies(cookies)
                .execute()
    }

    fun getAbsoluteUrl(url: String): String {
        return root + url
    }
}
