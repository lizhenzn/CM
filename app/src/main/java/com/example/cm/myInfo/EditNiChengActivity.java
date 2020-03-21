package com.example.cm.myInfo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.example.cm.R;

public class EditNiChengActivity extends AppCompatActivity {
private EditText niCheng_ed;
private SharedPreferences sharedPreferences;
private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_nicheng);
        niCheng_ed=(EditText)findViewById(R.id.edit_nichen);
        sharedPreferences=getSharedPreferences("myInfo",MODE_PRIVATE);
        editor=sharedPreferences.edit();
        Intent intent=getIntent();
        String nic=intent.getStringExtra("user");
        niCheng_ed.setText(nic);
        Toolbar toolbar=(Toolbar)findViewById(R.id.niCheng_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
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
                //保存昵称进shareprefence，并且上传服务器
                editor.putString("user", String.valueOf(niCheng_ed.getText()));
                editor.apply();
                this.finish();           //保存直接退出这个活动
            }break;
                default:break;
        }

        return true;
    }
}
