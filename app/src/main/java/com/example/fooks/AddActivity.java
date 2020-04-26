package com.example.fooks;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.fooks.entity.Book;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;

import cz.msebera.android.httpclient.Header;

public class AddActivity extends Activity {

    private static final int FILE_SELECT_CODE = 0;
    private String mUsername;
    protected static final String TAG = "AddActivity";
    private EditText editText;
    private ProgressBar progressBar;
    private TextView textView;
    private TextView textView2;
    private Button button1;
    private Button button2;
    private Book mBook=new Book();

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                String path = (String) msg.obj;
                editText.setText(path);
            }
            if (msg.what == 2) {
                int progress = (int) msg.arg1;
                AddActivity.this.progressBar.setProgress(progress);
                int a = msg.arg2;
                textView.setText(a + "%");
            }
            if (msg.what == 3) {
                String string = (String) msg.obj;
                textView2.setText("" + string);
            }
        }
    };

    @Override
    protected void onPause() {
        overridePendingTransition(0,0);
        super.onPause();
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        if (ContextCompat.checkSelfPermission(AddActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(AddActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE} ,1);
        }

        initView();
        Intent intent =getIntent();
        mUsername =intent.getStringExtra("username");

        initListener();



    }

    private void initListener() {
        button1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showFileChooser();
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                UpLoad();
            }
        });

    }

    /**
     * 上传
     */
    protected void UpLoad() {
        progressBar.setProgress(0);
        AsyncHttpClient client = new AsyncHttpClient();//实例化上传对象
        String url = "http://47.94.229.72:8080/Fooks/DServlet";//url组成：ip:端口 + 服务端工程名 + servlet名
        String path = editText.getText().toString().trim();

        if (null != path && "" != path) {
            File file = new File(path);
            if (file.exists() && file.length() > 0) {
                RequestParams params = new RequestParams();
                try {
                    params.put("profile", file);//将文件加入参数
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //上传文件
                client.post(url, params, new AsyncHttpResponseHandler() {

                    @Override//失败的监听
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        Message msg = handler.obtainMessage();
                        msg.what = 3;
                        msg.obj = "上传失败！";
                        handler.sendMessage(msg);
                        error.printStackTrace();
                    }

                    @Override//成功的监听
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        Message msg = handler.obtainMessage();
                        msg.what = 3;
                        msg.obj = "上传成功！";
                        handler.sendMessage(msg);

                        try {
                            String result = new String(responseBody,"utf-8");
                            Log.e(TAG,"返回结果"+result);
                            try {
                                JSONObject jsonObject=new JSONObject(result);
                                Book book =new Book();
                                book.setBookName(jsonObject.getString("bookname"));
                                book.setBookPath(jsonObject.getString("savepath"));
                                book.setCreateUser(mUsername);
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
                                Date date = new Date();
                                String createdate = simpleDateFormat.format(date);
                                book.setCreateDate(createdate);
                                Log.e(TAG,"书籍信息"+book);
                                AddBook(book);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override//动态变化
                    public void onProgress(final long bytesWritten, final long totalSize) {
                        super.onProgress(bytesWritten, totalSize);
                        progressBar.setMax((int) totalSize);
                        float a = (float) bytesWritten / (float) totalSize;
                        Message msg = handler.obtainMessage();
                        msg.what = 2;
                        msg.arg1 = (int) bytesWritten;
                        msg.arg2 = (int) (a * 100f);
                        handler.sendMessage(msg);
                    }
                });
            }

        }
    }

    private void AddBook(Book book) {
        AsyncHttpClient client =new AsyncHttpClient();
        String url = "http://47.94.229.72:8080/Fooks/AddBookServlet";//url组成：ip:端口 + 服务端工程名 + servlet名
        RequestParams params = new RequestParams();
        params.put("bookname",book.getBookName());
        params.put("bookpath",book.getBookPath());
        params.put("createuser",book.getCreateUser());
        params.put("createdate",book.getCreateDate());
        client.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                if(i == 200) {
                    try {
                        String result = new String(bytes, "utf-8");
                        try {
                            JSONObject jsonObject = new JSONObject(result);
                            int s = jsonObject.getInt("result");
                            switch (s){
                                case 0:
                                    Toast.makeText(getApplicationContext(),"入库失败",Toast.LENGTH_LONG).show();
                                    break;
                                case 1:
                                    Toast.makeText(getApplicationContext(),"成功添加入库",Toast.LENGTH_LONG).show();
                                    Intent intent=new Intent(AddActivity.this,ReadActivity.class);
                                    intent.putExtra("username",mUsername);
                                    startActivity(intent);
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

    /**
     * 选择文件
     */
    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");//过滤文件类型（所有）
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(Intent.createChooser(intent, "请选择文件！"), FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "未安装文件管理器！", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case FILE_SELECT_CODE:
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();
                    String path = FileUtils.getPath(this, uri);//得到文件路径
                    Message msg = handler.obtainMessage();
                    msg.what = 1;
                    msg.obj = path;
                    handler.sendMessage(msg);
                }
                break;
        }
    }

    /**
     * 文件工具类
     * @author ql
     *
     */
    static class FileUtils {
        public static String getPath(Context context, Uri uri) {

            if ("content".equalsIgnoreCase(uri.getScheme())) {
                String[] projection = { "_data" };
                Cursor cursor = null;

                try {
                    cursor = context.getContentResolver().query(uri, projection, null, null, null);
                    int column_index = cursor.getColumnIndexOrThrow("_data");
                    if (cursor.moveToFirst()) {
                        return cursor.getString(column_index);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if ("file".equalsIgnoreCase(uri.getScheme())) {
                return uri.getPath();
            }
            return null;
        }
    }



    private void initView() {
        editText = (EditText) findViewById(R.id.editText1);
        progressBar = (ProgressBar) findViewById(R.id.progressBar1);
        textView = (TextView) findViewById(R.id.textView1);
        textView2 = (TextView) findViewById(R.id.textView2);
        button1 = (Button) findViewById(R.id.get_book);
        button2 = (Button)findViewById(R.id.up_book);
    }
}
