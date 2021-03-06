/*
 *  Copyright (c) 2017 Tran Le Duy
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

package com.duy.pascal.ui.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.duy.pascal.ui.R;
import com.duy.pascal.ui.code.CompileManager;
import com.duy.pascal.ui.editor.EditorActivity;
import com.duy.pascal.ui.file.FileManager;
import com.duy.pascal.ui.runnable.ExecuteActivity;
import com.duy.pascal.ui.utils.DLog;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;


public class SplashScreenActivity extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST = 11;
    private static final String TAG = "SplashScreenActivity";
    private static final int REQUEST_CHECK_LICENSE = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PreferenceManager.setDefaultValues(this, R.xml.pref_settings, false);
        setContentView(R.layout.activity_splash);
        if (!permissionGranted()) {
            requestPermission();
        } else {
            startMainActivity();
        }
    }

    private void requestPermission() {
        String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
        ActivityCompat.requestPermissions(this, permissions, MY_PERMISSIONS_REQUEST);
    }

    private boolean permissionGranted() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED)
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;
            }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startMainActivity();
                } else {
                    TextView txtMsg = findViewById(R.id.txt_msg);
                    txtMsg.setText(R.string.permission_denied_storage);
                    findViewById(R.id.btn_enable).setVisibility(View.VISIBLE);
                    findViewById(R.id.btn_enable).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            gotoSystemSetting();
                        }
                    });
                }
            }
        }
    }

    private void gotoSystemSetting() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }


    /**
     * If receive data from other app (it could be file, text from clipboard),
     * You will be handle data and send to {@link EditorActivity}
     */
    private void startMainActivity() {
        Intent data = getIntent();
        String action = data.getAction();
        DLog.d(TAG, "startMainActivity: action = " + action);

        String type = data.getType();
        Intent intentEdit = new Intent(this, EditorActivity.class);
        if (action != null && Intent.ACTION_SEND.equals(action) && type != null) {
            FirebaseAnalytics.getInstance(this).logEvent("open_from_clipboard", new Bundle());
            if (type.equals("text/plain")) {
                handleActionSend(data, intentEdit);
            }
        } else if (action != null && Intent.ACTION_VIEW.equals(action) && type != null) {
            FirebaseAnalytics.getInstance(this).logEvent("open_from_another", new Bundle());
            handleActionView(data, intentEdit);
        } else if (action != null && action.equalsIgnoreCase("run_from_shortcut")) {
            FirebaseAnalytics.getInstance(this).logEvent("run_from_shortcut", new Bundle());
            handleRunProgram(data);
            return;
        }

        intentEdit.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intentEdit);
        overridePendingTransition(0, 0);
        finish();

    }

    private void handleRunProgram(Intent data) {
        Intent runIntent = new Intent(this, ExecuteActivity.class);
        runIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        Serializable file = data.getSerializableExtra(CompileManager.EXTRA_FILE);
        runIntent.putExtra(CompileManager.EXTRA_FILE, file);
        overridePendingTransition(0, 0);
        startActivity(runIntent);
        finish();
    }

    private void handleActionView(@NonNull Intent from, @NonNull Intent to) {
        if (from.getData() == null || from.getType() == null) {
            return;
        }
        DLog.d(TAG, "handleActionView() called with: from = [" + from + "], to = [" + to + "]");
        if (from.getData().toString().endsWith(".pas") || from.getData().toString().endsWith(".txt")) {
            Uri uriPath = from.getData();
            DLog.d(TAG, "handleActionView: " + uriPath.getPath());
            try {
                String filePath = FileManager.getPathFromUri(this, uriPath);
                if (filePath != null) {
                    to.putExtra(CompileManager.EXTRA_FILE, new File(filePath));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (from.getType().equals("text/x-pascal")) {
            Uri uri = from.getData();
            try {
                //clone file
                InputStream inputStream = getContentResolver().openInputStream(uri);
                FileManager fileManager = new FileManager(this);
                File file = fileManager.createRandomFile(this);
                fileManager.copy(inputStream, new FileOutputStream(file));

                to.putExtra(CompileManager.EXTRA_FILE, file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleActionSend(Intent from, Intent to) {
        String text = from.getStringExtra(Intent.EXTRA_TEXT);
        FileManager fileManager = new FileManager(this);
        File file = fileManager.createRandomFile(this);
        fileManager.saveFile(file, text);
        to.putExtra(CompileManager.EXTRA_FILE, file);
    }
}