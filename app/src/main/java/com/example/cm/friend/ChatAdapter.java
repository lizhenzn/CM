package com.example.cm.friend;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class ChatAdapter extends BaseAdapter {
    private List<Message> messageList;
    private Context context;
    public ChatAdapter(Context context,List<Message> messageList){
        this.context=context;
        this.messageList=messageList;
    }
    @Override
    public int getCount() {
        return messageList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }
    class ViewHolder{
        ImageView imageView;
        TextView nameTV;
        TextView mesTV;

    }
}
