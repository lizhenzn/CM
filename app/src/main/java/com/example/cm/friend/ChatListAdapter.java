package com.example.cm.friend;

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

public class ChatListAdapter extends BaseAdapter implements View.OnClickListener,View.OnTouchListener {
   // private  ArrayList<ChatListMessage> listMessageArrayList;          //会话列表的类列表 image、name、message、time、type
    private List<HashMap<String,Object>> list;
    private DisplayMetrics displayMetrics;    //屏幕大小
    private Context context;
    private View beforeView;
    private List<ViewHolder> viewHolders;
    public ChatListAdapter(){}
    public ChatListAdapter(List<HashMap<String,Object>>list,Context context,DisplayMetrics displayMetrics){
        this.list=list;
        this.context=context;
        this.displayMetrics=displayMetrics;
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
            viewHolder.hsv=(HorizontalScrollView)convertView.findViewById(R.id.hsv);
            viewHolder.relativeLayout=(RelativeLayout) convertView.findViewById(R.id.chat_relativeL);
            viewHolder.headImage=(ImageView)convertView.findViewById(R.id.chat_iv);
            viewHolder.nameTV=(TextView) convertView.findViewById(R.id.chat_XMtv);

            viewHolder.mesTV=(TextView) convertView.findViewById(R.id.chat_Megtv);
            viewHolder.timeTV=(TextView) convertView.findViewById(R.id.Meg_time);
            //添加标记 按钮
            viewHolder.topBtn=(Button)convertView.findViewById(R.id.ZD_btn);
            viewHolder.topBtn.setTag(position);
            viewHolder.deleteBtn=(Button)convertView.findViewById(R.id.delete_btn);
            viewHolder.deleteBtn.setTag(position);
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
        ViewGroup.LayoutParams params=viewHolder.relativeLayout.getLayoutParams();
        params.width=displayMetrics.widthPixels;

        Map<String,Object> map=list.get(position);
        viewHolder.headImage.setImageDrawable(context.getResources().getDrawable(Integer.parseInt(map.get("headImage")+"")));
        viewHolder.nameTV.setText(map.get("name")+"");
        viewHolder.mesTV.setText(map.get("message")+"");
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("EE HH:MMM");
        String time=simpleDateFormat.format(new Date());
        //viewHolder.timeTV.setText(map.get("time")+"");
        viewHolder.timeTV.setText(time);
        //注册点击事件
        viewHolder.hsv.setOnTouchListener(this);
        //viewHolder.hsv.setOnClickListener(this);
        //viewHolder.headImage.setOnClickListener(this);
        //viewHolder.linearLayout1.setOnTouchListener(this);
        //viewHolder.linearLayout2.setOnTouchListener(this);
        viewHolder.topBtn.setOnClickListener(this);
        viewHolder.deleteBtn.setOnClickListener(this);

        return convertView;
    }

    @Override
    public void onClick(View v) {
        int position=(int)v.getTag();
        switch(v.getId()){
            case R.id.ZD_btn:{
                HashMap<String,Object>map=list.get(position);
                list.remove(position);
                list.add(0,map);
            }break;
            case R.id.delete_btn:{
                list.remove(position);
            }break;
            /*case R.id.chat_ll1:{
                Intent intent[]=new Intent[1];
                intent[0] = new Intent(context, MyInfoActivity.class);
                context.startActivities(intent);
            }break;

            case R.id.chat_ll2:{
                Intent intent[]=new Intent[1];
                intent[0] = new Intent(context, MyInfoActivity.class);
                context.startActivities(intent);
            }break;*/
            default:break;
        }
        //恢复
        ViewHolder viewHolder=viewHolders.get(position);
        viewHolder.hsv.scrollTo(0,0);
        notifyDataSetChanged();

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
            float down = 0;//按下坐标
            float up = 0;//抬起坐标
            ViewHolder holder = (ViewHolder) v.getTag();
            switch (event.getAction()) {
                //按下动作
                case MotionEvent.ACTION_DOWN:
                    down = event.getX();
                    if (beforeView != null) {
                        //将上一次操作的滚动条恢复原样
                        ViewHolder beforeHolder = (ViewHolder) beforeView.getTag();
                        beforeHolder.hsv.smoothScrollTo(0, 0);
                    }
                    //这是一次测试
                    break;
                //抬起动作
                case MotionEvent.ACTION_UP:
                    //记录此次操作
                    beforeView = v;
                    up = event.getX();
                    if (down == up) {
                        // 手势未移动，即点击
                        Intent intent[]=new Intent[1];
                        intent[0]=new Intent(context,MyInfoActivity.class);
                        context.startActivities(intent);
                        Log.d("233333", "onTouch: ");
                        Toast.makeText(context,"233333",Toast.LENGTH_LONG).show();
                    } else {
                        int scrol = holder.hsv.getScrollX();
                        if (scrol < 100) {
                            //滚动距离小于屏幕1/4，回滚
                            holder.hsv.smoothScrollTo(0, 0);
                        }
                        else {
                            //移动到最后端
                            holder.hsv.smoothScrollTo(1000, 0);
                        }
                    }
                    /**
                     * 返回true，表示不需要父组件再去处理此事件
                     * 返回false，不能实现上面的全滚动效果
                     */
                    return true;
            }
            return false;
        }




    class ViewHolder{
        HorizontalScrollView hsv;
        RelativeLayout relativeLayout;
        ImageView headImage;
        TextView  nameTV;
        TextView mesTV;
        TextView timeTV;
        Button topBtn;
        Button deleteBtn;
    }

}
