package com.example.cm.friend;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cm.R;
import com.example.cm.util.Connect;

public class AddFriendAdapter extends BaseAdapter {
    private  Context context;
    public AddFriendAdapter(Context context){
        this.context=context;
    }
    @Override
    public int getCount() {
        return Connect.addFriendItemList.size();
    }

    @Override
    public Object getItem(int position) {
        return Connect.addFriendItemList.get(position);
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
            convertView= LayoutInflater.from(context).inflate(R.layout.add_friend_item,parent,false);
            viewHolder.headImage=(ImageView)convertView.findViewById(R.id.add_friendIM);
            viewHolder.userName=(TextView)convertView.findViewById(R.id.add_friendNM);
            viewHolder.reason=(TextView)convertView.findViewById(R.id.add_friendReason);
            viewHolder.result=(TextView)convertView.findViewById(R.id.add_friend_resultTV);
            viewHolder.accept=(Button)convertView.findViewById(R.id.add_friend_acceptBtn);
            viewHolder.reject=(Button)convertView.findViewById(R.id.add_friend_rejectBtn);
            convertView.setTag(viewHolder);
        }else{
            viewHolder= (ViewHolder) convertView.getTag();
        }
        viewHolder.headImage.setImageBitmap(Connect.addFriendItemList.get(position).getFriendInfo().getHeadBt());
        viewHolder.userName.setText(Connect.addFriendItemList.get(position).getFriendInfo().getUserName());
        viewHolder.reason.setText(Connect.addFriendItemList.get(position).getReason());
        viewHolder.result.setText(Connect.addFriendItemList.get(position).getResult());
       // viewHolder.accept.setOnClickListener();
        //viewHolder.reject.setOnClickListener();

        return convertView;
    }
    class ViewHolder{
        ImageView headImage;
        TextView userName;
        TextView reason;
        TextView result;
        Button reject;
        Button accept;
    }
}
