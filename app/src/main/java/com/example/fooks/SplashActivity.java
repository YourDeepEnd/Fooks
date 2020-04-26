package com.example.fooks;
import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.lang.ref.WeakReference;

public class SplashActivity extends Activity {

    public static final int CODE = 1001;
    private TextView mtextView;
    @Override
    protected void onPause() {
        overridePendingTransition(0,0);
        super.onPause();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (ContextCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(SplashActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE} ,1);
        }
        setContentView(R.layout.activity_splash);

        mtextView = (TextView) findViewById(R.id.text_daojishi);

        final MyHandler handler = new MyHandler(this);
        Message message = Message.obtain();
        message.what = CODE;
        message.arg1 = 5000;
        handler.sendMessage(message);

        mtextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转
                LoginActivity.start(SplashActivity.this);
                SplashActivity.this.finish();
                handler.removeMessages(CODE);
            }
        });
    }

    public static class MyHandler extends Handler{

        public final WeakReference<SplashActivity> mWeakReference;
        public MyHandler(SplashActivity activity) {
            mWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage (Message msg) {
            super.handleMessage(msg);
            SplashActivity activity = mWeakReference.get();
            if (msg.what == CODE) {
                if (activity != null){
                    int time = msg.arg1;
                    activity.mtextView.setText(time/1000+"秒，点击跳过");

                    Message message = Message.obtain();
                    message.what = CODE;
                    message.arg1 = time - 1000;

                    if(time > 0) {
                        sendMessageDelayed(message,1000);
                    } else {
                        //跳转
                        LoginActivity.start(activity);
                        activity.finish();
                    }
                }
            }
        }
    }
}
