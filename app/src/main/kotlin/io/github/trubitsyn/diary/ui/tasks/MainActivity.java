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

package io.github.trubitsyn.diary.ui.tasks;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Spanned;
import android.text.method.ArrowKeyMovementMethod;
import android.text.method.LinkMovementMethod;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Date;
import java.util.List;

import io.github.trubitsyn.diary.R;
import io.github.trubitsyn.diary.api.LocalDateTimeUtil;
import io.github.trubitsyn.diary.api.RemoteDataSource;
import io.github.trubitsyn.diary.databinding.MainActivityBinding;
import io.github.trubitsyn.diary.ui.calendar.CalendarActivity;
import io.github.trubitsyn.diary.ui.login.CredentialsProvider;
import io.github.trubitsyn.diary.ui.login.LoginActivity;

public class MainActivity extends AppCompatActivity implements MainViewModel.DataListener {
    private static final int CALENDAR_REQUEST_CODE = 0;
    private MainActivityBinding binding;
    private MainViewModel viewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        viewModel = new MainViewModel(new RemoteDataSource(this), this);
        binding.setViewModel(viewModel);

        LocalDateTime today = LocalDateTimeUtil.todayDateTime();
        viewModel.fetch(LocalDateTimeUtil.dateToFetch(today));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void onActionModeStarted(ActionMode mode) {
        super.onActionModeStarted(mode);
        binding.tasksView.setMovementMethod(ArrowKeyMovementMethod.getInstance());
    }

    @Override
    public void onActionModeFinished(ActionMode mode) {
        super.onActionModeFinished(mode);
        binding.tasksView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    public void onDateSelect(MenuItem item) {
        Intent intent = new Intent(this, CalendarActivity.class);
        startActivityForResult(intent, CALENDAR_REQUEST_CODE);
    }

    public void onLogout(MenuItem item) {
        CredentialsProvider credentialsProvider = new CredentialsProvider(this);
        credentialsProvider.removeSaved();

        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    @Override
    public void onBeginFetching(LocalDate date) {
        String dateText = getDateText(date);
        setTitle(getTitleWithDate(dateText));
    }

    @Override
    public void onBeginFetchingRange(LocalDate from, LocalDate to) {
        String dateText = getDateRangeText(from, to);
        setTitle(getTitleWithDate(dateText));
    }

    @Override
    public void onDataFetched(String data) {
        Spanned html = Html.fromHtml(data);
        binding.tasksView.setText(html);
        binding.tasksView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private String getTitleWithDate(String date) {
        return getString(R.string.tasks_for_date, date);
    }

    private String getDateText(LocalDate date) {
        return getFormatter().print(date);
    }

    private String getDateRangeText(LocalDate from, LocalDate to) {
        final DateTimeFormatter fmt = getFormatter();
        return fmt.print(from) + "-" + fmt.print(to);
    }

    private DateTimeFormatter getFormatter() {
        return DateTimeFormat.forPattern("dd.MM");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == CALENDAR_REQUEST_CODE) {
            List<Date> dates = (List<Date>) data.getSerializableExtra("dates");
            List<LocalDate> localDates = LocalDateTimeUtil.datesToLocalDates(dates);

            if (localDates.size() > 1) {
                viewModel.fetch(localDates);
            } else {
                viewModel.fetch(localDates.get(0));
            }
        }
    }
}
