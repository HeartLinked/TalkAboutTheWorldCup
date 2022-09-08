package com.ss.video.rtc.demo.quickstart;


import android.Manifest;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.ss.rtc.demo.quickstart.R;
import com.ss.video.rtc.demo.quickstart.pickvideo.VideoFragment;

public class MainActivity2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("lfyActivity2", "QAQ");
        super.onCreate(savedInstanceState);
        Log.d("lfyActivity2", "QAQ");
        setContentView(R.layout.activity_main);
        Log.d("lfyActivity2", "QAQ");
        initOperation();

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);

    }

    private void initOperation() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fl_content,
                        new VideoFragment())
                .commit();
    }


}