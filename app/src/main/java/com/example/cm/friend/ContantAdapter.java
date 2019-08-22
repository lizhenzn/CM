package com.example.cm.friend;

import android.content.ClipData;
import android.content.Context;
import android.support.constraint.Group;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cm.friend.chat.GroupInfo;
import com.example.cm.myInfo.FriendInfo;
import com.example.cm.myInfo.SmackUserInfo;

import com.example.cm.R;
import com.example.cm.myInfo.SmackUserInfo;
import com.example.cm.util.Connect;

import java.util.List;

import main.UserInfo;

public class ContantAdapter extends BaseExpandableListAdapter {
    private Context context;
    public ContantAdapter(Context context){
        this.context=context;
    }
    @Override
    public int getGroupCount() {
        return Connect.groupInfoList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return Connect.groupInfoList.get(groupPosition).getFriendInfoList().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return Connect.groupInfoList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return Connect.groupInfoList.get(groupPosition).getFriendInfoList().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    //当子ID相同时是否复用
    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if(convertView==null)
            convertView=View.inflate(context, R.layout.expend_group,null);
        TextView groupTV=(TextView)convertView.findViewById(R.id.expand_groupTV);
        groupTV.setText(Connect.groupInfoList.get(groupPosition).getGroupName());
        if(isExpanded){//列表展开

        }else{//列表没有展开

        }
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ViewHolder childViewHolder=null;
        if(convertView==null){
            childViewHolder=new ViewHolder();
            convertView=View.inflate(context,R.layout.expand_child,null);
            childViewHolder.headIV=(ImageView)convertView.findViewById(R.id.contant_groupIV);
            childViewHolder.userTV=(TextView) convertView.findViewById(R.id.contant_groupTV);
            childViewHolder.headIV.setImageBitmap(Connect.groupInfoList.get(groupPosition).getFriendInfoList().get(childPosition).getHeadBt());
            childViewHolder.userTV.setText(Connect.groupInfoList.get(groupPosition).getFriendInfoList().get(childPosition).getUserName());
            convertView.setTag(childViewHolder);
        }else{
            childViewHolder= (ViewHolder) convertView.getTag();
        }
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
    class ViewHolder{
        ImageView headIV;
        TextView userTV;
    }
}


