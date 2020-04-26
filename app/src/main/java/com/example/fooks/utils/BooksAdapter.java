package com.example.fooks.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.fooks.R;
import com.example.fooks.ReadActivity;
import com.example.fooks.entity.Book;

import java.util.List;

public class BooksAdapter extends BaseAdapter {
    private List<Book> mList;
    private Context mContext;
    private LayoutInflater mLayoutInflater;


    public BooksAdapter(List<Book> mList, Context mContext) {
        this.mList = mList;
        this.mContext = mContext;
        this.mLayoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView==null)
        {
            viewHolder= new ViewHolder();
            convertView=mLayoutInflater.inflate(R.layout.booklist,null);
            viewHolder.mBook= convertView.findViewById(R.id.book_show);
            convertView.setTag(viewHolder);
        }else {
            viewHolder=(ViewHolder)convertView.getTag();
        }

        Book book=mList.get(position);
        viewHolder.mBook.setText(book.getBookName());

        return convertView;
    }
}
