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

package io.github.trubitsyn.diary.ui.tasks

import android.content.Context
import android.databinding.ObservableInt
import android.view.View
import android.widget.Toast
import io.github.trubitsyn.diary.R
import io.github.trubitsyn.diary.datasource.DataSource
import io.github.trubitsyn.diary.datasource.LocalDataSource
import io.github.trubitsyn.diary.datasource.RemoteDataSource
import io.github.trubitsyn.diary.task.SimpleTaskFormatter
import io.github.trubitsyn.diary.task.Task
import io.github.trubitsyn.diary.task.TaskFormatter
import io.github.trubitsyn.diary.task.TasksMerger
import io.github.trubitsyn.diary.ui.ViewModel
import org.joda.time.LocalDate
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.lang.kotlin.FunctionSubscriber
import rx.schedulers.Schedulers
import java.util.*

class MainViewModel(private val context: Context, private val listener: DataListener) : ViewModel {
    val progressVisibility = ObservableInt(View.VISIBLE)
    val dataVisibility = ObservableInt(View.GONE)
    val errorVisibility = ObservableInt(View.GONE)
    private val formatter = SimpleTaskFormatter()
    private val localDataSource: DataSource = LocalDataSource()
    private var remoteDataSource: DataSource? = RemoteDataSource(context)
    private var currentDataSource: DataSource? = remoteDataSource

    fun fetch(nextStudyDayDate: LocalDate) {
        listener.onBeginFetching(nextStudyDayDate)
        showProgress()

        fetchData(nextStudyDayDate).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(FunctionSubscriber<List<Task>>()
                .onNext {
                    val formatted = appendEntries(it, formatter)
                    listener.onDataFetched(formatted)
                }
                .onCompleted {
                    showData()
                }
                .onError {
                    currentDataSource = localDataSource
                    showUsingLocalData()
                    fetch(nextStudyDayDate)
                }
        )
    }

    fun fetch(dates: List<LocalDate>) {
        listener.onBeginFetchingRange(dates[0], dates[dates.size - 1])
        showProgress()

        val listOfLists = ArrayList<List<Task>>()

        fetchData(dates).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(FunctionSubscriber<List<Task>>()
                .onNext {
                    listOfLists.add(it)
                }
                .onCompleted {
                    showData()

                    val mergedTasks = TasksMerger(listOfLists.flatten()).merge()
                    val formatted = appendEntries(mergedTasks, formatter)
                    listener.onDataFetched(formatted)
                }
                .onError {
                    currentDataSource = localDataSource
                    showUsingLocalData()
                    fetch(dates)
                }
        )
    }

    private fun showProgress() {
        progressVisibility.set(View.VISIBLE)
        dataVisibility.set(View.GONE)
        errorVisibility.set(View.GONE)
    }

    private fun showData() {
        progressVisibility.set(View.GONE)
        errorVisibility.set(View.GONE)
        dataVisibility.set(View.VISIBLE)
    }

    private fun showError() {
        progressVisibility.set(View.GONE)
        dataVisibility.set(View.GONE)
        errorVisibility.set(View.VISIBLE)
    }

    private fun showUsingLocalData() {
        Toast.makeText(context, R.string.local_data_used, Toast.LENGTH_SHORT).show()
    }

    private fun appendEntries(tasks: List<Task>, formatter: TaskFormatter): String {
        val builder = StringBuilder()
        for (task in tasks) {
            builder.append(formatter.format(task))
        }
        return builder.toString()
    }

    private fun fetchData(dates: List<LocalDate>): Observable<List<Task>> {
        return Observable.create { subscriber ->
            for (date in dates) {
                subscriber.onNext(currentDataSource?.getTasks(date))
            }
            subscriber.onCompleted()
        }
    }

    private fun fetchData(date: LocalDate): Observable<List<Task>> {
        return Observable.create { subscriber ->
            subscriber.onNext(currentDataSource?.getTasks(date))
            subscriber.onCompleted()
        }
    }

    override fun destroy() {
        currentDataSource = null
        remoteDataSource = null
    }

    interface DataListener {
        fun onBeginFetching(date: LocalDate)
        fun onBeginFetchingRange(from: LocalDate, to: LocalDate)
        fun onDataFetched(data: String)
    }
}
