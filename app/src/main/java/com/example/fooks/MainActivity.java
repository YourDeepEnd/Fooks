package com.example.fooks;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {
    private Button mHome;
    private Button mBook;
    private Button mPerson;
    private EditText editText;
    private EditText editText2;
    //设置返回按钮：不应该退出程序---而是返回桌面
    //复写onKeyDown事件
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent home = new Intent(Intent.ACTION_MAIN);
            home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            home.addCategory(Intent.CATEGORY_HOME);
            startActivity(home);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    @Override
    protected void onPause() {
        overridePendingTransition(0,0);
        super.onPause();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        //
        mBook=(Button) this.findViewById(R.id.btn_book);
        mHome=(Button) this.findViewById(R.id.btn_home);
        mPerson=(Button) this.findViewById(R.id.btn_person);

        //把输入框变成分割线 by：scf
        editText=this.findViewById(R.id.editText);
        editText2=this.findViewById(R.id.editText2);
        editText.setFocusableInTouchMode(false);//不可编辑
        editText.setKeyListener(null);
        editText2.setFocusableInTouchMode(false);//不可编辑
        editText2.setKeyListener(null);

        //
        mPerson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,UserActivity.class);
                startActivity(intent);
                MainActivity.this.overridePendingTransition(0, 0);

            }
        });
        mBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,ReadActivity.class);
                startActivity(intent);
                MainActivity.this.overridePendingTransition(0, 0);
            }
        });
    }
}
