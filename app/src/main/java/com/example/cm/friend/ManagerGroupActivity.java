package com.example.cm.friend;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.cm.R;
import com.example.cm.util.Connect;

import org.jivesoftware.smack.SmackException;


public class ManagerGroupActivity extends AppCompatActivity {
private EditText groupName_et;
private Button add_group_btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_manager_group);
        init();
        add_group_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String groupName=String.valueOf(groupName_et.getText());
                if(groupName.length()==0){              //分组名为空
                    Toast.makeText(ManagerGroupActivity.this,"分组名不能为空",Toast.LENGTH_SHORT).show();

                }else if(!Connect.isLogined){
                    Toast.makeText(ManagerGroupActivity.this,"未登录",Toast.LENGTH_SHORT).show();

                }else{
                    while(!Connect.roster.isLoaded()){
                        try {
                            Connect.roster.reload();
                        } catch (SmackException.NotLoggedInException e) {
                            e.printStackTrace();
                        } catch (SmackException.NotConnectedException e) {
                            e.printStackTrace();
                        }
                    }
                    Connect.roster.createGroup(groupName);
                    Toast.makeText(ManagerGroupActivity.this,"添加分组成功",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public void init(){
        groupName_et=(EditText)findViewById(R.id.edit_groupName);
        add_group_btn=(Button)findViewById(R.id.add_group_btn);
    }
}
