package com.example.fooks;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.fooks.entity.Book;
import com.example.fooks.utils.BooksAdapter;
import com.example.fooks.utils.ViewHolder;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import java.util.List;

import cz.msebera.android.httpclient.Header;


public class ReadActivity extends Activity {
    private String mUsername;
    private Button mHome;
    private Button mBook;
    private Button mPerson;
    private Button mUpload;
    private EditText editText;
    private EditText editText2;
    private ListView mListView;
    private List<Book> mShowBooks  =new ArrayList<>();
    private List <ViewHolder> mViewHolder;
    private static final int FILE_SELECT_CODE = 0;
    private static String TAG="ReadActivity";
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
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read);
        //把输入框变成分割线 by：scf
        editText=this.findViewById(R.id.editText);
        editText2=this.findViewById(R.id.editText2);
        editText.setFocusableInTouchMode(false);//不可编辑
        editText.setKeyListener(null);
        editText2.setFocusableInTouchMode(false);//不可编辑
        editText2.setKeyListener(null);

        Intent intent =getIntent();
        mUsername =intent.getStringExtra("username");

        mListView=(ListView) this.findViewById(R.id.book_list) ;
        //初始化控件
        initView();

        initListener();

        initBook();

    }

    private void initBook() {
        ListView ShowBookList=(ListView)findViewById(R.id.book_list);

        //获取数据
        AsyncHttpClient client =new AsyncHttpClient();
        String url = "http://47.94.229.72:8080/Fooks/BookListServlet";//url组成：ip:端口 + 服务端工程名 + servlet名
        RequestParams params = new RequestParams();
        params.put("username",mUsername);
        client.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                if(i == 200) {
                    try {
                        String result = new String(bytes,"utf-8");
                        Log.e(TAG,"返回结果"+result);
                        try {
                            JSONArray   jsonArray = new JSONArray(result);
                            for(int j=0;j<jsonArray.length();j++){
                                JSONObject jsonObject=jsonArray.getJSONObject(j);
                                Book book =new Book();
                                book.setId(jsonObject.getInt("id"));
                                book.setBookName(jsonObject.getString("bookName"));
                                book.setBookPath(jsonObject.getString("bookPath"));
                                book.setCreateUser(jsonObject.getString("createUser"));
                                book.setCreateDate(jsonObject.getString("createDate"));
                                mShowBooks.add(book);
                            }
                            BooksAdapter booksAdapter=new BooksAdapter(mShowBooks,ReadActivity.this);
                            ShowBookList.setAdapter(booksAdapter);

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

    private void initListener() {
        mUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upLoad();

            }
        });
        mHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReadActivity.this,MainActivity.class);
                startActivity(intent);
                ReadActivity.this.overridePendingTransition(0, 0);

            }
        });
        mPerson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReadActivity.this,UserActivity.class);
                startActivity(intent);
                ReadActivity.this.overridePendingTransition(0, 0);

            }
        });
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Book book=mShowBooks.get(position);
                String path=Environment.getExternalStorageDirectory()+"/Fooks/Book/"+book.getBookName();
                File file =new File(path);
                if(file.exists()){
                    Intent intent =new Intent(ReadActivity.this,BookActivity.class);
                    intent.putExtra("username",mUsername);
                    intent.putExtra("bookName",book.getBookName());
                    intent.putExtra("path",path);
                    startActivity(intent);

                }else {
                    Toast.makeText(getApplicationContext(),"开始下载，请耐心等待",Toast.LENGTH_LONG).show();
                    Log.e(TAG,"book:"+book);
                    Log.e(TAG,"path:"+path);
                    DownLoadBook(book,path);
                }
            }
        });
    }
    @Override
    protected void onPause() {
        overridePendingTransition(0,0);
        super.onPause();
    }
    private void DownLoadBook(Book book,String path) {
        AsyncHttpClient client =new AsyncHttpClient();
        String url = "http://47.94.229.72:8080/Fooks/Book/";//url组成：ip:端口 + 服务端工程名 + servlet名
        try {
            String URL=URLEncoder.encode(book.getBookName(),"utf-8");
            URL=URL.replaceAll("\\+", "%20");
            Log.e(TAG,url+URL);

        client.post(url+URL, new FileAsyncHttpResponseHandler(new File(path)) {
            @Override
            public void onFailure(int i, Header[] headers, Throwable throwable, File file) {
                Toast.makeText(getApplicationContext(),"下载失败，请检查网络",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSuccess(int i, Header[] headers, File file) {
//                try {//此try-catch块中的代码将下载到的文件保存在一个目录下
//                    // Context.getFilesDir()，该方法返回/data/data/[youPackageName]/files的File对象
//                    FileOutputStream fileOutputStream = new FileOutputStream(path);
//                    FileInputStream fileInputStream = new FileInputStream(file);
//                    byte[] bytes = new byte[(int) file.length()];
//                    fileInputStream.read(bytes);
//                    fileOutputStream.write(bytes);
//                    fileOutputStream.flush();
//                    fileInputStream.close();
//                    fileOutputStream.close();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
                Toast.makeText(getApplicationContext(),"下载成功，点击打开",Toast.LENGTH_LONG).show();
            }
        });

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

    private void upLoad() {
        Intent intent = new Intent(ReadActivity.this,AddActivity.class);
        intent.putExtra("username",mUsername);
        startActivity(intent);

    }





    private void initView() {
        mBook=(Button) this.findViewById(R.id.btn_book);
        mHome=(Button) this.findViewById(R.id.btn_home);
        mPerson=(Button) this.findViewById(R.id.btn_person);
        mUpload=(Button) this.findViewById(R.id.book_upload);
    }
}

