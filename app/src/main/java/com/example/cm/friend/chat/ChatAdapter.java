package com.example.cm.friend.chat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cm.R;
import com.example.cm.util.Connect;
import com.example.cm.util.EmoticonsTextView;

import java.util.List;

public class ChatAdapter extends BaseAdapter implements View.OnClickListener {
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
        return messageList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder=null;
        if(convertView==null){
            viewHolder=new ViewHolder();
            if(messageList.get(position).getType()==1) {
                convertView = LayoutInflater.from(context).inflate(R.layout.chat_item_self,  null);
            }else {
                convertView = LayoutInflater.from(context).inflate(R.layout.chat_item_friend, null);
            }
            viewHolder.imageView=(ImageView)convertView.findViewById(R.id.chat_itemIV);
            viewHolder.nameTV=(EmoticonsTextView)convertView.findViewById(R.id.chat_itemName);
            viewHolder.mesTV=(EmoticonsTextView)convertView.findViewById(R.id.chat_itemMes);
            convertView.setTag(viewHolder);
        }else{
            viewHolder= (ViewHolder) convertView.getTag();
        }
        viewHolder.imageView.setImageResource(R.drawable.cm);
        viewHolder.imageView.setOnClickListener(this);
        if(messageList.get(position).getType()==1) {
            viewHolder.nameTV.setText(messageList.get(position).getFrom());
        }else {
            viewHolder.nameTV.setText(messageList.get(position).getFrom());
        }
        viewHolder.mesTV.setText(messageList.get(position).getBody());
        return convertView;
    }

    @Override
    public void onClick(View v) {

    }

    class ViewHolder{
        ImageView imageView;
        TextView nameTV;
        TextView mesTV;

    }
}
