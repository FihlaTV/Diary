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

package io.github.trubitsyn.diary.api;

import org.joda.time.DateTimeConstants;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class LocalDateTimeUtil {

    public static LocalDate dateToFetch(LocalDateTime today) {
        LocalDate date = today.toLocalDate();
        if (isStudyDay(date)) {
            if (lessonsHaveFinished(today)) {
                return tomorrowStudyDayDate(date);
            }
            return date;
        }
        return tomorrowStudyDayDate(date);
    }

    private static boolean lessonsHaveFinished(LocalDateTime today) {
        return today.hourOfDay().get() >= 15;
    }

    public static LocalDateTime todayDateTime() {
        return LocalDateTime.now(getDefaultTimeZone());
    }

    private static LocalDate september1st(int year) {
        Calendar september1st = Calendar.getInstance();
        september1st.set(Calendar.MONTH, Calendar.SEPTEMBER);
        september1st.set(Calendar.DAY_OF_MONTH, 1);
        september1st.set(Calendar.YEAR, year);

        return LocalDate.fromCalendarFields(september1st);
    }

    public static LocalDate minAvailableDate(LocalDate today) {
        int studyYearStartedIn = yearWhenStudyYearStarts(today);
        return september1st(studyYearStartedIn);
    }

    public static LocalDate maxAvailableDate(LocalDate today) {
        return today.plusWeeks(1);
    }

    private static DateTimeZone getDefaultTimeZone() {
        return DateTimeZone.forOffsetHours(3);
    }

    private static boolean isStudyDay(LocalDate date) {
        return date.dayOfWeek().get() != DateTimeConstants.SUNDAY;
    }

    private static LocalDate tomorrowStudyDayDate(LocalDate today) {
        return nextStudyDayDate(today);
    }

    private static LocalDate nextStudyDayDate(LocalDate today) {
        return today.plusDays(nextStudyDayOffset(today.dayOfWeek().get()));
    }

    private static int nextStudyDayOffset(int todayWeekDay) {
        return (todayWeekDay == DateTimeConstants.SATURDAY) ? 2 : 1;
    }

    private static int yearWhenStudyYearStarts(LocalDate today) {
        int month = today.getMonthOfYear();
        if (month < DateTimeConstants.SEPTEMBER) {
            return today.getYear() - 1;
        }
        return today.getYear();
    }

    public static List<LocalDate> datesToLocalDates(List<Date> dates) {
        List<LocalDate> localDates = new ArrayList<>();

        for (Date date : dates) {
            localDates.add(LocalDate.fromDateFields(date));
        }
        return localDates;
    }

}
