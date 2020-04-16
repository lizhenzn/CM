package com.example.cm.myInfo;

import android.graphics.Color;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.cm.R;
import com.example.cm.theme.ThemeColor;
import com.example.cm.util.Connect;
import com.example.cm.util.MessageManager;

public class EditEmailActivity extends AppCompatActivity implements View.OnClickListener{
private Button save_email_btn;
private EditText email_et;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_edit_email);
        save_email_btn=(Button)findViewById(R.id.save_email_btn);
        email_et=(EditText)findViewById(R.id.edit_email);
        save_email_btn.setOnClickListener(this);
        Toolbar toolbar=(Toolbar)findViewById(R.id.email_toolbar);
        ThemeColor.setTheme(EditEmailActivity.this,toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.save_email_btn:{
                String email= String.valueOf(email_et.getText());
                String reg="[\\w-]+(\\.[\\w-]+)*@([\\w-]+\\.)+[a-zA-Z]+$";
                if(email.matches(reg)){
                    VCardManager.setSelfInfo(Connect.getXMPPTCPConnection(),"email",email);
                    MessageManager.getSmackUserInfo().setEmail(email);
                    Toast.makeText(EditEmailActivity.this,"修改邮箱成功",Toast.LENGTH_SHORT).show();
                    Log.e("", "onClick: 更改邮箱为："+email );
                }else{
                    Toast.makeText(EditEmailActivity.this,"邮箱格式不正确",Toast.LENGTH_SHORT).show();
                }
            }break;
            default:break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:this.finish();break;
            default:break;
        }
        return true;
    }
}
