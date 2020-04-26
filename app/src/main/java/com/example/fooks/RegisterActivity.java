package com.example.fooks;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fooks.entity.User;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.mob.MobSDK;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import cz.msebera.android.httpclient.Header;

public class RegisterActivity extends AppCompatActivity {
    private Button buttonCode,buttonRegister;
    private EditText editTextPhoneNum,editTextCode;
    private EditText mR1Password;
    private EditText mR2Password;
    private String phoneNum,code;
    private String pwd1,pwd2;
    private EventHandler eh;
    private String TAG="RegisterActivity";

    public static void start(Context context) {
        Intent intent = new Intent(context,RegisterActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onPause() {
        overridePendingTransition(0,0);
        super.onPause();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initView();


        initListener();

        MobSDK.init(this);


    }

    public void initListener() {

        eh = new EventHandler() {
            @Override
            public void afterEvent(int event, int result, Object data) {
                if (result == SMSSDK.RESULT_COMPLETE){
                    //回调完成
                    if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                        //提交验证码成功
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(RegisterActivity.this,"注册成功",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }else if (event == SMSSDK.EVENT_GET_VOICE_VERIFICATION_CODE){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(RegisterActivity.this,"语音验证发送",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE){
                        //获取验证码成功
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(RegisterActivity.this,"验证码已发送",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }else if (event == SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES){
                        Log.i("test","test");
                    }
                }else{
                    ((Throwable)data).printStackTrace();
                    Throwable throwable = (Throwable) data;
                    throwable.printStackTrace();
                    Log.i("1234",throwable.toString());
                    try {
                        JSONObject obj = new JSONObject(throwable.getMessage());
                        final String des = obj.optString("detail");
                        if (!TextUtils.isEmpty(des)){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
//                                    Toast.makeText(RegisterActivity.this,des,Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        //注册一个事件回调监听，用于处理SMSSDK接口请求的结果
        SMSSDK.registerEventHandler(eh);
        buttonCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phoneNum = editTextPhoneNum.getText().toString().trim();

                if(phoneNum.length()==11){
                    // 获取验证码

                    SMSSDK.getVerificationCode("86", phoneNum);
                }else {
                    Toast.makeText(getApplicationContext(),"请输入手机号",Toast.LENGTH_LONG).show();
                    return;
                }
            }
        });
        //点击事件
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phoneNum = editTextPhoneNum.getText().toString().trim();
                code = editTextCode.getText().toString().trim();
                pwd1 =mR1Password.getText().toString().trim();
                pwd2 =mR2Password.getText().toString().trim();
                if(phoneNum.length()!=11){
                    Toast.makeText(getApplicationContext(),"请输入正确的手机号",Toast.LENGTH_LONG).show();
                }else if(pwd1.equals("")){
                    Toast.makeText(getApplicationContext(),"请输入密码",Toast.LENGTH_LONG).show();
                }else if(pwd2.equals("")){
                    Toast.makeText(getApplicationContext(),"请输入确认密码",Toast.LENGTH_LONG).show();
                }else if(!pwd1.equals(pwd2)){
                    Toast.makeText(getApplicationContext(),"两次输入密码不一致",Toast.LENGTH_LONG).show();
                }else if(code.isEmpty()){
                    Toast.makeText(getApplicationContext(),"请输入验证码",Toast.LENGTH_LONG).show();
                }else{
                    SMSSDK.submitVerificationCode("86", phoneNum, code);
                    User user =new User();
                    user.setUsername(phoneNum);
                    user.setPassword(pwd1);
                    Log.e(TAG,"用户名"+user.getUsername());
                    Log.e(TAG,"密码"+user.getPassword());
                    Register(user);
                    return;
                }
            }
        });

    }

    protected void Register(User user) {
        AsyncHttpClient client =new AsyncHttpClient();
        String url = "http://47.94.229.72:8080/Fooks/RegisterServlet";//url组成：ip:端口 + 服务端工程名 + servlet名
        RequestParams params = new RequestParams();
        params.put("username",user.getUsername());
        params.put("password",user.getPassword());
        client.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                if(i == 200) {
                    try {
                        String result = new String(bytes, "utf-8");
                        try {
                            JSONObject jsonObject = new JSONObject(result);
                            int s = jsonObject.getInt("result");
                            switch (s) {
                                case 0:
                                    Toast.makeText(getApplicationContext(), "该用户名已被使用", Toast.LENGTH_LONG).show();
                                    break;
                                case 1:
                                    Toast.makeText(getApplicationContext(), "注册成功", Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(RegisterActivity.this, ReadActivity.class);
                                    intent.putExtra("username",phoneNum);
                                    startActivity(intent);
                                    break;
                                case 2:
                                    Toast.makeText(getApplicationContext(), "系统错误", Toast.LENGTH_LONG).show();
                                    break;
                                default:
                                    Toast.makeText(getApplicationContext(), "未知错误", Toast.LENGTH_LONG).show();
                                    break;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        Toast.makeText(RegisterActivity.this, result, Toast.LENGTH_SHORT).show();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                Toast.makeText(getApplicationContext(),"请求失败，请检查网络",Toast.LENGTH_LONG).show();
            }
        });
    }

    private void initView() {
        //初始化控件
        buttonCode = findViewById(R.id.buttonCode);
        buttonRegister = findViewById(R.id.buttonRegister);
        editTextCode = findViewById(R.id.editTextCode);
        editTextPhoneNum = findViewById(R.id.editTextPhoneNum);
        mR1Password=findViewById(R.id.r1_password);
        mR2Password=findViewById(R.id.r2_password);
    }

    // 使用完EventHandler需注销，否则可能出现内存泄漏
    @Override
    protected void onDestroy() {
        super.onDestroy();
        SMSSDK.unregisterEventHandler(eh);
    }



}

