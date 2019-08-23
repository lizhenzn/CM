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
            if(messageList.get(position).getType()==1) {   //自己发送的信息
                if(messageList.get(position).getMessageType().equals("text")) //文本信息
                    convertView = LayoutInflater.from(context).inflate(R.layout.chat_item_self,  null);
                else  //图片信息
                    convertView = LayoutInflater.from(context).inflate(R.layout.chat_item_self_photo,  null);
            }else {                                          //接收到的信息
                if(messageList.get(position).getMessageType().equals("text")) //文本信息
                     convertView = LayoutInflater.from(context).inflate(R.layout.chat_item_friend, null);
                else       //图片信息
                    convertView = LayoutInflater.from(context).inflate(R.layout.chat_item_friend_photo,  null);
            }
            viewHolder.imageView=(ImageView)convertView.findViewById(R.id.chat_itemIV);
            viewHolder.nameTV=(EmoticonsTextView)convertView.findViewById(R.id.chat_itemName);
            if(messageList.get(position).getMessageType().equals("text"))
                viewHolder.mesTV=(EmoticonsTextView)convertView.findViewById(R.id.chat_itemMes);
            else
                viewHolder.photoIV=(ImageView)convertView.findViewById(R.id.chat_item_photo);
            convertView.setTag(viewHolder);
        }else{
            viewHolder= (ViewHolder) convertView.getTag();
        }
        viewHolder.imageView.setImageResource(R.drawable.cm);
        viewHolder.imageView.setOnClickListener(this);
        if(messageList.get(position).getType()==1) {
            viewHolder.nameTV.setText(messageList.get(position).getFrom());
        }
        if(messageList.get(position).getMessageType().equals("text"))
            viewHolder.mesTV.setText(messageList.get(position).getBody());
        else {
            //viewHolder.photoIV.setImageResource(R.drawable.cm);
            //viewHolder.photoIV.setImageDrawable(Connect.getUserImage(Connect.xmpptcpConnection.getUser().split("/")[0]));
            viewHolder.photoIV.setImageBitmap(messageList.get(position).getPhoto());
        }
          //viewHolder.photoIV.setImageBitmap(messageList.get(position).getPhoto());
        return convertView;
    }

    @Override
    public void onClick(View v) {

    }

    class ViewHolder{
        ImageView imageView;
        TextView nameTV;
        TextView mesTV;
        ImageView photoIV;
    }
}
