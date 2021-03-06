package com.example.cm.friend.chat;

import android.content.Context;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cm.R;
import com.example.cm.myInfo.MyInfoActivity;
import com.example.cm.util.Connect;
import com.example.cm.util.DateFormatUtil;
import com.example.cm.util.MessageManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import q.rorbin.badgeview.QBadgeView;

import static com.example.cm.R.drawable.cm;

public class ChatListAdapter extends BaseAdapter  {
   // private  ArrayList<ChatListMessage> listMessageArrayList;          //会话列表的类列表 image、name、message、time、type
    //private DisplayMetrics displayMetrics;    //屏幕大小
    private Context context;
    private List<ViewHolder> viewHolders;
    public ChatListAdapter(){}
    public ChatListAdapter(Context context){
        this.context=context;

        viewHolders=new ArrayList<>();
    }

    @Override
    public int getCount() {
        return MessageManager.getFriendInfoList().size();
    }

    @Override
    public Object getItem(int position) {
        return MessageManager.getMessageMap().get(MessageManager.getFriendInfoList().get(position).getUserName());
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder=null;
        if(convertView==null){
            convertView= LayoutInflater.from(context).inflate(R.layout.chat_lv_item,null);
            viewHolder=new ViewHolder();
            //绑定布局
            viewHolder.relativeLayout=convertView.findViewById(R.id.chat_lv_item_re);
            viewHolder.headImage=(ImageView)convertView.findViewById(R.id.chat_iv);
            viewHolder.nameTV=(TextView) convertView.findViewById(R.id.chat_XMtv);

            viewHolder.mesTV=(TextView) convertView.findViewById(R.id.chat_Megtv);
            viewHolder.timeTV=(TextView) convertView.findViewById(R.id.Meg_time);
            //添加标记 按钮
            /*viewHolder.topBtn=(Button)convertView.findViewById(R.id.ZD_btn);
            viewHolder.topBtn.setTag(position);
            viewHolder.deleteBtn=(Button)convertView.findViewById(R.id.delete_btn);
            viewHolder.deleteBtn.setTag(position);*/
            //
            convertView.setTag(viewHolder);

        }
        else{
             viewHolder= (ViewHolder) convertView.getTag();
        }
        if(!viewHolders.contains(viewHolder)){
                 viewHolders.add(viewHolder);
        }
        //设置左边的LinearLayout宽度为屏幕宽度
        //ViewGroup.LayoutParams params=viewHolder.relativeLayout.getLayoutParams();
       // params.width=displayMetrics.widthPixels;
           //设置会话列表信息显示各个好友最后一条信息时间、内容
        String userName= MessageManager.getFriendInfoList().get(position).getUserName();
        String noteName= MessageManager.getFriendInfoList().get(position).getNoteName();
        List<Message> messageList=MessageManager.getMessageMap().get(userName);            //对应friendName的聊天信息列表
        //viewHolder.headImage.setImageDrawable(context.getResources().getDrawable(Integer.parseInt(map.get(Connect.friendInfoList.get(position).get("friendName").getHeadBt())));
        //viewHolder.headImage.setImageBitmap(map.get(Connect.friendInfoList.get(position).get("friendInfo").getHeadBt()));
        //viewHolder.headImage.setImageResource(cm);
        viewHolder.headImage.setImageBitmap(MessageManager.getFriendInfoList().get(position).getHeadBt());
        viewHolder.nameTV.setText(noteName);
        QBadgeView qBadgeView=new QBadgeView(context);
        qBadgeView.bindTarget(viewHolder.headImage);
        qBadgeView.setBadgeNumber(MessageManager.getUnReadCountByName(userName));
        if(messageList.size()>=1) {
            viewHolder.mesTV.setText(messageList.get(messageList.size() - 1).getBody());  //设置为最后一条信息
            long time=messageList.get(messageList.size()-1).getDate();
            Log.d("时间", "getView: "+time);
            String timeStr = DateFormatUtil.getDateStr(time);  //最后一条信息的时间
            Log.e("", "getView: "+timeStr );
            viewHolder.timeTV.setText(timeStr);
        }
        else
        {
            viewHolder.mesTV.setText(null);  //设置为最后一条信息//
            viewHolder.timeTV.setText("");
        }
        viewHolder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                qBadgeView.setBadgeNumber(0);
                Intent intent=new Intent(context,ChatActivity.class);
                intent.putExtra("userName", MessageManager.getFriendInfoList().get(position).getUserName());
                intent.putExtra("noteName",MessageManager.getFriendInfoList().get(position).getNoteName());
                context.startActivity(intent);

            }
        });


        return convertView;
    }


    class ViewHolder{
        RelativeLayout relativeLayout;
        ImageView headImage;
        TextView  nameTV;
        TextView mesTV;
        TextView timeTV;
    }

}
