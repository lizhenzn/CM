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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatListAdapter extends BaseAdapter  {
   // private  ArrayList<ChatListMessage> listMessageArrayList;          //会话列表的类列表 image、name、message、time、type
    private List<HashMap<String,Object>> list;
    //private DisplayMetrics displayMetrics;    //屏幕大小
    private Context context;
    private View beforeView;
    private List<ViewHolder> viewHolders;
    public ChatListAdapter(){}
    public ChatListAdapter(List<HashMap<String,Object>>list,Context context){
        this.list=list;
        this.context=context;

        viewHolders=new ArrayList<>();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
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
            //viewHolder.hsv=(HorizontalScrollView)convertView.findViewById(R.id.hsv);
            //viewHolder.relativeLayout=(RelativeLayout) convertView.findViewById(R.id.chat_relativeL);
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

        Map<String,Object> map=list.get(position);
        viewHolder.headImage.setImageDrawable(context.getResources().getDrawable(Integer.parseInt(map.get("headImage")+"")));
        viewHolder.nameTV.setText(map.get("name")+"");
        viewHolder.mesTV.setText(map.get("message")+"");
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("EE HH:MMM");
        String time=simpleDateFormat.format(new Date());
        //viewHolder.timeTV.setText(map.get("time")+"");
        viewHolder.timeTV.setText(time);



        return convertView;
    }


    class ViewHolder{
        ImageView headImage;
        TextView  nameTV;
        TextView mesTV;
        TextView timeTV;
    }

}
