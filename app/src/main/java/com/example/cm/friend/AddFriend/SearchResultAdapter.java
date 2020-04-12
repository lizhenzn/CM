package com.example.cm.friend.AddFriend;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cm.R;
import com.example.cm.myInfo.FriendInfo;
import com.example.cm.myInfo.VCardManager;
import com.example.cm.util.Connect;
import com.example.cm.util.MessageManager;

import java.util.ArrayList;

public class SearchResultAdapter extends BaseAdapter {
    private ArrayList<SearchResultItem> searchResultItems;
    private Context context;
    public SearchResultAdapter(ArrayList<SearchResultItem> searchResultItems,Context context){
        this.searchResultItems=searchResultItems;
        this.context=context;
    }
    @Override
    public int getCount() {
        return searchResultItems.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder=null;
        if(convertView==null){
            viewHolder=new ViewHolder();
            convertView= LayoutInflater.from(context).inflate(R.layout.search_friend_result_item,parent,false);
            viewHolder.headBitmapIV=(ImageView)convertView.findViewById(R.id.search_friend_headImage);
            viewHolder.userNameTV=(TextView)convertView.findViewById(R.id.search_friend_userTV);
            viewHolder.addBtn=(Button)convertView.findViewById(R.id.search_friend_btn);
            convertView.setTag(viewHolder);
        }else{
            viewHolder= (ViewHolder) convertView.getTag();
        }
        viewHolder.headBitmapIV.setImageBitmap(searchResultItems.get(position).getBitmap());
        viewHolder.userNameTV.setText(searchResultItems.get(position).getUser());
        viewHolder.addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 //发送添加请求
                    boolean contain=false;
                    for(int j=0;j<MessageManager.getContantFriendInfoList().size();j++){
                        if(MessageManager.getContantFriendInfoList().get(j).getUserName().equals(searchResultItems.get(position).getUser())){
                            contain=true;
                            break;
                        }
                    }

                    if(!contain) {
                        String user=searchResultItems.get(position).getUser();
                        AddFriendItem addFriendItem = new AddFriendItem();
                        FriendInfo friendInfo = new FriendInfo();
                        friendInfo.setUserName(user);
                        friendInfo.setHeadBt(VCardManager.getUserImage(user));
                        addFriendItem.setFriendInfo(friendInfo);
                        addFriendItem.setReason("Hello,World!");
                        addFriendItem.setResult("已发送验证");

                        MessageManager.setAddFriendItemListChanged(true);

                        try {
                            while(!Connect.getRoster().isLoaded()){
                                Connect.getRoster().reload();
                            }
                            Connect.getRoster().createEntry(user+"@"+Connect.SERVERNAME,user,new String[]{"Friends"});
                            Log.e("ADD", "onClick: 申请发送成功");
                        } catch (Exception e) {//SmackException.NotConnectedException
                            e.printStackTrace();
                            addFriendItem.setResult("申请发送异常，请重试");
                            Toast.makeText(context, "申请发送异常", Toast.LENGTH_SHORT).show();
                        }
                        MessageManager.getAddFriendItemList().add(addFriendItem);
                    }else{
                        Toast.makeText(context,"已有此好友",Toast.LENGTH_SHORT).show();
                    }

            }
        });

        return convertView;
    }
    class ViewHolder{
        ImageView headBitmapIV;
        TextView userNameTV;
        Button addBtn;
    }
}
