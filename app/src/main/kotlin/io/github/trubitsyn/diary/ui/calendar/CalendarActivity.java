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

package io.github.trubitsyn.diary.ui.calendar;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import com.squareup.timessquare.CalendarPickerView;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import io.github.trubitsyn.diary.R;
import io.github.trubitsyn.diary.api.LocalDateTimeUtil;
import io.github.trubitsyn.diary.databinding.CalendarActivityBinding;

public class CalendarActivity extends AppCompatActivity {
    private CalendarActivityBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_calendar);

        LocalDateTime today = LocalDateTimeUtil.todayDateTime();

        LocalDate min = LocalDateTimeUtil.minAvailableDate(today.toLocalDate());
        LocalDate max = LocalDateTimeUtil.maxAvailableDate(today.toLocalDate());

        LocalDate selectedDate = LocalDateTimeUtil.dateToFetch(today);

        binding.calendarView
                .init(min.toDate(), max.toDate())
                .withSelectedDate(selectedDate.toDate())
                .inMode(CalendarPickerView.SelectionMode.RANGE);

        binding.calendarView.setDateSelectableFilter(new StudyDateFilter());
        binding.calendarView.setOnInvalidDateSelectedListener(new NonStudyDateListener());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.calendar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public void onDateSelected(MenuItem item) {
        List<Date> dates = binding.calendarView.getSelectedDates();

        Intent intent = new Intent();
        intent.putExtra("dates", (Serializable) dates);

        setResult(RESULT_OK, intent);
        finish();
    }
}
