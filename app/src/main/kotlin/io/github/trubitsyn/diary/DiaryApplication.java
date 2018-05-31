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

package io.github.trubitsyn.diary;

import android.app.Application;
import android.content.Context;

import com.squareup.leakcanary.LeakCanary;

import net.danlew.android.joda.JodaTimeAndroid;

import io.github.trubitsyn.diary.api.DataSource;
import io.github.trubitsyn.diary.api.NetworkClient;

public class DiaryApplication extends Application {
    private DataSource dataSource;
    private NetworkClient networkClient;

    @Override
    public void onCreate() {
        super.onCreate();
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }

        JodaTimeAndroid.init(this);
        LeakCanary.install(this);

        dataSource = null;
        networkClient = null;
    }

    public static DiaryApplication getInstance(Context context) {
        return (DiaryApplication) context.getApplicationContext();
    }

    public NetworkClient getNetworkClient() {
        return networkClient;
    }
}
