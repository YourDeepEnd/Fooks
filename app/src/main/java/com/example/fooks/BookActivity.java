package com.example.fooks;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;


import com.tencent.smtt.sdk.TbsReaderView;

import java.io.File;

public class BookActivity extends Activity implements TbsReaderView.ReaderCallback {
    private TbsReaderView mTbsReaderView;
    private  com.tencent.smtt.sdk.WebView mTBWebview;
//    com.tencent.smtt.sdk.WebView webView = new com.tencent.smtt.sdk.WebView(this);
//
//    int width = webView.getView().getWidth();

    private String mUsername;
    private String mPath;
    private String mBookName;
    private String TAG="BookActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);


        Intent intent =getIntent();
        mUsername =intent.getStringExtra("username");
        mBookName=intent.getStringExtra("bookName");
        mPath=intent.getStringExtra("path");
        Log.e(TAG,"传书用户名"+mUsername);
        Log.e(TAG,"书名"+mBookName);
        Log.e(TAG,"路径"+mPath);
        initView();
        OpenBook();
    }

    private void OpenBook() {
        File file =new File(mPath);
        if (!file.exists()) {
            Log.d("print","准备创建/TbsReaderTemp！！");
            boolean mkdir = file.mkdir();
            if(!mkdir){
                Log.d("print","创建/TbsReaderTemp失败！！！！！");
            }
        }
        Bundle bundle = new Bundle();
   /*   1.TbsReader: Set reader view exception:Cannot add a null child view to a ViewGroup
        TbsReaderView: OpenFile failed! [可能是文件的路径错误]*/
   /*   2.插件加载失败
        so文件不支持;*/
   /*
   ndk {
            //设置支持的SO库架构  'arm64-v8a',
            abiFilters 'armeabi', "armeabi-v7a",  'x86'
        } */
   /*
        3.自适应大小
    */
        Log.d("print","filePath"+mPath);//可能是路径错误
        Log.d("print","tempPath"+Environment.getExternalStorageDirectory().getPath());
        bundle.putString("filePath", mPath);
        bundle.putString("tempPath", Environment.getExternalStorageDirectory().getPath());
        boolean result = mTbsReaderView.preOpen(getFileType(mBookName), false);
        if (result) {
            mTbsReaderView.openFile(bundle);
        }
    }




    private void initView() {
        mTbsReaderView=new TbsReaderView (this, this);
        mTBWebview= findViewById(R.id.tbsView);
        com.tencent.smtt.sdk.WebView webView = new com.tencent.smtt.sdk.WebView(this);

        int width = webView.getView().getWidth();
        mTBWebview.addView(mTbsReaderView,new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
    }

    private String getFileType(String paramString) {
        String str = "";
        if (TextUtils.isEmpty(paramString)) {
            return str;
        }
        int i = paramString.lastIndexOf('.');
        if (i <= -1) {
            return str;
        }
        str = paramString.substring(i + 1);
        return str;
    }
    @Override
    protected void onStop() {
        super.onStop ();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy ();
        mTbsReaderView.onStop ();
    }


    @Override
    public void onCallBackAction(Integer integer, Object o, Object o1) {

    }
    @Override
    protected void onPause() {
        overridePendingTransition(0,0);
        super.onPause();
    }
}
