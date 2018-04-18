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

package io.github.trubitsyn.diary.ui.login;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Process;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import io.github.trubitsyn.diary.R;
import io.github.trubitsyn.diary.api.NetworkClient;
import io.github.trubitsyn.diary.api.auth.Credentials;
import io.github.trubitsyn.diary.databinding.LoginActivityBinding;
import io.github.trubitsyn.diary.ui.tasks.MainActivity;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class LoginActivity extends AppCompatActivity implements LoginViewModel.DataListener {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final CredentialsProvider credentialsProvider = new CredentialsProvider(this);

        if (credentialsProvider.hasCredentials()) {
            final Credentials credentials = credentialsProvider.retrieve();

            Observable.create(new Observable.OnSubscribe<Void>() {
                @Override
                public void call(Subscriber<? super Void> subscriber) {
                    try {
                        NetworkClient.INSTANCE.auth(credentials);
                    } catch (Exception e) {
                        subscriber.onError(e);
                    }
                    subscriber.onCompleted();
                }
            }).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<Void>() {
                        @Override
                        public void onCompleted() {
                            onLoginFinished();
                        }

                        @Override
                        public void onError(Throwable e) {
                            initView(credentialsProvider);
                            Toast.makeText(LoginActivity.this, R.string.error_network, Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onNext(Void aVoid) {

                        }
                    });
        } else {
            initView(credentialsProvider);
        }
    }

    private void initView(CredentialsProvider credentialsProvider) {
        LoginActivityBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        binding.setViewModel(new LoginViewModel(this, credentialsProvider, this));
    }

    @Override
    public void onLoginFinished() {
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        finish();
    }
}
