package com.example.cm.friend;

import android.content.ClipData;
import android.content.Context;
import android.support.constraint.Group;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cm.R;

import java.util.List;

public class ContantAdapter extends BaseExpandableListAdapter {
    private List<GroupInfo> groupList;
    private Context context;
    public ContantAdapter(Context context,List<GroupInfo> groupInfoList){
        this.context=context;
        this.groupList=groupInfoList;
    }
    @Override
    public int getGroupCount() {
        return groupList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return groupList.get(groupPosition).getUserInfoList().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groupList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return groupList.get(groupPosition).getUserInfoList().get(childPosition);
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
        groupTV.setText(groupList.get(groupPosition).getGroupName());
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
            childViewHolder.headIV.setImageResource(groupList.get(groupPosition).getUserInfoList().get(childPosition).getHeadPhoto());
            childViewHolder.userTV.setText(groupList.get(groupPosition).getUserInfoList().get(childPosition).getUserName());
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
class GroupInfo{
    private String GroupName;
    private List<UserInfo> userInfoList;

    public void setGroupName(String groupName) {
        GroupName = groupName;
    }

    public String getGroupName() {
        return GroupName;
    }

    public List<UserInfo> getUserInfoList() {
        return userInfoList;
    }

    public void setUserInfoList(List<UserInfo> userInfoList) {
        this.userInfoList = userInfoList;
    }
}
class UserInfo {
    private int headPhoto;
    private String userName;

    public int getHeadPhoto() {
        return headPhoto;
    }

    public void setHeadPhoto(int headPhoto) {
        this.headPhoto = headPhoto;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
