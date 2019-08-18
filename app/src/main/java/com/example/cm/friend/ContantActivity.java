package com.example.cm.friend;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ExpandableListView;

import com.example.cm.R;

import java.util.ArrayList;
import java.util.List;

public class ContantActivity extends AppCompatActivity {
    private List<GroupInfo> groupInfoList;
    private ContantAdapter contantAdapter;
    private ExpandableListView expandableListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contant);
        init();
        initData();
        expandableListView.setAdapter(contantAdapter);

    }
    public void init(){
        groupInfoList=new ArrayList<>();
        expandableListView=(ExpandableListView)findViewById(R.id.expendLV);
        contantAdapter=new ContantAdapter(this,groupInfoList);
    }
    public void initData(){
        GroupInfo groupInfo=null;
        UserInfo userInfo=null;
        List<UserInfo> userInfoList;
        for(int i=0;i<6;i++){
            groupInfo=new GroupInfo();
            groupInfo.setGroupName("GROUP"+i);
            userInfoList=new ArrayList<>();
            for(int j=0;j<6;j++){
                userInfo=new UserInfo();
                userInfo.setHeadPhoto(R.drawable.ic_launcher_background);
                userInfo.setUserName("USER"+j);
                userInfoList.add(userInfo);
            }
            groupInfo.setUserInfoList(userInfoList);
            groupInfoList.add(groupInfo);
        }
    }
}
