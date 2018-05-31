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

package io.github.trubitsyn.diary.credentials;

import android.content.Context;
import android.content.SharedPreferences;

public class CredentialsProvider {
    private static final String PREFS_NAME = "default";
    private static final String FIELD_LOGIN = "login";
    private static final String FIELD_PASSWORD = "password";
    private final SharedPreferences preferences;

    public CredentialsProvider(Context context) {
        this.preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public Credentials retrieve() {
        String login = preferences.getString(FIELD_LOGIN, null);
        String password = preferences.getString(FIELD_PASSWORD, null);
        return new Credentials(login, password);
    }

    public void save(Credentials credentials) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(FIELD_LOGIN, credentials.getLogin());
        editor.putString(FIELD_PASSWORD, credentials.getPassword());
        editor.apply();
    }

    public void removeSaved() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(FIELD_LOGIN).remove(FIELD_PASSWORD);
        editor.apply();
    }

    public boolean hasCredentials() {
        return preferences.contains(FIELD_LOGIN) && preferences.contains(FIELD_PASSWORD);
    }
}
