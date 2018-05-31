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

package io.github.trubitsyn.diary.ui.login;

import android.content.Context;
import android.databinding.ObservableBoolean;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import io.github.trubitsyn.diary.DiaryApplication;
import io.github.trubitsyn.diary.R;
import io.github.trubitsyn.diary.api.NetworkClient;
import io.github.trubitsyn.diary.api.auth.Credentials;
import io.github.trubitsyn.diary.ui.ViewModel;
import rx.Single;
import rx.SingleSubscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class LoginViewModel implements ViewModel {
    public final Credentials credentials;
    public final ObservableBoolean loginEnabled;
    private final DataListener listener;
    private Context context;
    private CredentialsProvider credentialsProvider;
    private NetworkClient networkClient;

    public LoginViewModel(Context context, CredentialsProvider credentialsProvider, DataListener listener) {
        this.context = context;
        this.credentialsProvider = credentialsProvider;
        this.listener = listener;
        this.credentials = credentialsProvider.retrieve();
        this.networkClient = DiaryApplication.getInstance(context).getNetworkClient();
        this.loginEnabled = new ObservableBoolean(true);
    }

    public void onLoginActionClick(View view) {
        loginEnabled.set(false);
        hideSoftKeyboard(view.getContext(), view);
        auth(credentials);
    }

    public void onLoginNoPasswordActionClick(View view) {
        loginEnabled.set(false);
        hideSoftKeyboard(view.getContext(), view);
        //auth(new DefaultCredentials());
        listener.onLoginFinished();
    }

    private void hideSoftKeyboard(Context context, View focus) {
        if (focus != null) {
            InputMethodManager imm = ((InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE));
            imm.hideSoftInputFromWindow(focus.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    private void auth(final Credentials credentials) {
        Single.create(new Single.OnSubscribe<Void>() {
            @Override
            public void call(SingleSubscriber<? super Void> singleSubscriber) {
                networkClient.auth(credentials);
                singleSubscriber.onSuccess(null);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new SingleSubscriber<Void>() {
            @Override
            public void onSuccess(Void value) {
                credentialsProvider.save(credentials);
                listener.onLoginFinished();
            }

            @Override
            public void onError(Throwable error) {
                displayError();
                loginEnabled.set(true);
            }
        });
    }

    private void displayError() {
        Toast.makeText(context, R.string.error_login, Toast.LENGTH_LONG).show();
    }

    @Override
    public void destroy() {
        context = null;
        credentialsProvider = null;
    }

    interface DataListener {
        void onLoginFinished();
    }
}
