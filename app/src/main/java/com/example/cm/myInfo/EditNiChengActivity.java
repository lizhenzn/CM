package com.example.cm.myInfo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;


import com.example.cm.R;
import com.example.cm.util.Connect;
import com.example.cm.util.MessageManager;

public class EditNiChengActivity extends AppCompatActivity {
private EditText niCheng_ed;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_nicheng);
        niCheng_ed=(EditText)findViewById(R.id.edit_nichen);
        Intent intent=getIntent();
        String nic=intent.getStringExtra("user");
        niCheng_ed.setText(nic);
        Toolbar toolbar=(Toolbar)findViewById(R.id.niCheng_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.save_btn,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:this.finish();break;
            case R.id.saveNiCheng_btn:{
                String niC=String.valueOf(niCheng_ed.getText());
                if(niC.length()>0) {
                    VCardManager.setSelfInfo(Connect.getXMPPTCPConnection(), "NickName", niC);
                    MessageManager.getSmackUserInfo().setNiC(niC);
                    Log.e("", "onOptionsItemSelected: 修改后昵称：" + niC);
                }else {
                    Toast.makeText(EditNiChengActivity.this,"昵称不能为空",Toast.LENGTH_SHORT).show();
                }
            }break;
                default:break;
        }

        return true;
    }
}
