package com.example.fooks;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.tencent.connect.UserInfo;
import com.tencent.connect.auth.QQToken;
import com.tencent.connect.common.Constants;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import androidx.annotation.Nullable;

import com.example.fooks.entity.User;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;

public class LoginActivity extends Activity {
    private static final String APP_ID = "1105602574";//官方获取的APPID
    private Tencent mTencent;
    private BaseUiListener mIUiListener;
    private UserInfo mUserInfo;
    private EditText mUsername;
    private EditText mPassword;
    private Button mLogin;
    private Button mGotoregister;
    private String username;
    private String password;
    private String TAG="LoginActivity";
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
    public static void start(Context context) {
        Intent intent = new Intent(context,LoginActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mTencent = Tencent.createInstance(APP_ID, LoginActivity.this.getApplicationContext());

        //初始化控件
        initView();
        //设置点击事件
        initListener();
    }
    public void buttonLogin(View v) {
        /**通过这句代码，SDK实现了QQ的登录，这个方法有三个参数，第一个参数是context上下文，第二个参数SCOPO 是一个String类型的字符串，表示一些权限
         官方文档中的说明：应用需要获得哪些API的权限，由“，”分隔。例如：SCOPE = “get_user_info,add_t”；所有权限用“all”
         第三个参数，是一个事件监听器，IUiListener接口的实例，这里用的是该接口的实现类 */
        mIUiListener = new BaseUiListener();
        //all表示获取所有权限
        mTencent.login(LoginActivity.this, "all", mIUiListener);
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
                                    Log.e(TAG,"当前qq已经注册");
                                case 1:
                                    Intent intent = new Intent(LoginActivity.this, ReadActivity.class);
                                    intent.putExtra("username",user.getUsername());
                                    startActivity(intent);
                                    Log.e(TAG,"通过qq登录接口注册成功");
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
    /**
     * 自定义监听器实现IUiListener接口后，需要实现的3个方法
     * onComplete完成 onError错误 onCancel取消
     */
    private class BaseUiListener implements IUiListener {

        @Override
        public void onComplete(Object response) {
            Toast.makeText(LoginActivity.this, "授权成功", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "response:" + response);
            JSONObject obj = (JSONObject) response;
            try {
                String openID = obj.getString("openid");

                String accessToken = obj.getString("access_token");
                String expires = obj.getString("expires_in");
                mTencent.setOpenId(openID);
                mTencent.setAccessToken(accessToken, expires);
                QQToken qqToken = mTencent.getQQToken();
                mUserInfo = new UserInfo(getApplicationContext(), qqToken);
                Log.e(TAG,"用户ID"+mUserInfo);

                mUserInfo.getUserInfo(new IUiListener() {
                    @Override
                    public void onComplete(Object response) {
                        User user =new User();
                        user.setUsername(openID);
                        user.setPassword("123");
                        Log.e(TAG,"用户名"+user.getUsername());
                        Log.e(TAG,"密码"+user.getPassword());
                        Register(user);
                        Log.e(TAG, "qq登录授权成功" + response.toString());
                    }

                    @Override
                    public void onError(UiError uiError) {
                        Log.e(TAG, "登录失败" + uiError.toString());
                    }

                    @Override
                    public void onCancel() {
                        Log.e(TAG, "登录取消");

                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onError(UiError uiError) {
            Toast.makeText(LoginActivity.this, "授权失败", Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onCancel() {
            Toast.makeText(LoginActivity.this, "授权取消", Toast.LENGTH_SHORT).show();

        }

    }

    /**
     * 在调用Login的Activity或者Fragment中重写onActivityResult方法
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.REQUEST_LOGIN) {
            Tencent.onActivityResultData(requestCode, resultCode, data, mIUiListener);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    private void initListener() {
        mGotoregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username=mUsername.getText().toString().trim();
                password=mPassword.getText().toString().trim();
                if(username.isEmpty()){
                    Toast.makeText(getApplicationContext(),"请输入账号",Toast.LENGTH_LONG).show();
                }else if(password.isEmpty()){
                    Toast.makeText(getApplicationContext(),"请输入密码",Toast.LENGTH_LONG).show();
                }else {
                    Login();
                }
            }
        });
    }

    protected void Login() {
        User user =new User();
        user.setUsername(username);
        user.setPassword(password);
        Log.e(TAG,"用户名"+user.getUsername());
        Log.e(TAG,"密码"+user.getPassword());
        AsyncHttpClient client = new AsyncHttpClient();//实例化上传对象
        String url = "http://47.94.229.72:8080/Fooks/LoginServlet";//url组成：ip:端口 + 服务端工程名 + servlet名
        RequestParams params = new RequestParams();
        params.put("username",user.getUsername());
        params.put("password",user.getPassword());
        client.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                if(i == 200) {
                    try {
                        String result = new String(bytes,"utf-8");
                        Log.e(TAG,"返回结果"+result);
                        try {
                            JSONObject jsonObject=new JSONObject(result);
                            int s=jsonObject.getInt("result");
                            switch (s){
                                case 0:
                                    Toast.makeText(getApplicationContext(),"该用户不存在",Toast.LENGTH_LONG).show();
                                    break;
                                case 1:
                                    Toast.makeText(getApplicationContext(),"登录成功",Toast.LENGTH_LONG).show();
                                    Intent intent=new Intent(LoginActivity.this,ReadActivity.class);
                                    intent.putExtra("username",username);
                                    startActivity(intent);
                                    break;
                                case 2:
                                    Toast.makeText(getApplicationContext(),"密码错误",Toast.LENGTH_LONG).show();
                                    break;
                                default:
                                    Toast.makeText(getApplicationContext(),"未知错误",Toast.LENGTH_LONG).show();
                                    break;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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
        mUsername=(EditText)this.findViewById(R.id.username);
        mPassword=(EditText)this.findViewById(R.id.password);
        mLogin=(Button) this.findViewById(R.id.login_btn);
        mGotoregister=(Button)this.findViewById(R.id.goto_register_btn);
    }
}
