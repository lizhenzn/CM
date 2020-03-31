package com.example.cm.myInfo;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.cm.R;
import com.example.cm.util.Connect;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.iqregister.AccountManager;

public class ChangePasswordActivity extends AppCompatActivity {
private EditText password_et;
private EditText insurePassword_et;
private Button change_btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        password_et=(EditText)findViewById(R.id.changePassword_et1);
        insurePassword_et=(EditText)findViewById(R.id.changePassword_et2);
        change_btn=(Button)findViewById(R.id.changePassword_btn);
        Toolbar toolbar=(Toolbar)findViewById(R.id.password_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }
        change_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if((TextUtils.isEmpty(password_et.getText().toString().trim()))
                        ||(TextUtils.isEmpty(insurePassword_et.getText().toString().trim()))){
                    Toast.makeText(ChangePasswordActivity.this,"输入不能为空",Toast.LENGTH_SHORT).show();
                }else if(!password_et.getText().toString().trim().equals(insurePassword_et.getText().toString().trim())){
                    Toast.makeText(ChangePasswordActivity.this,"两次输入密码不一致",Toast.LENGTH_SHORT).show();
                }else {
                    if(changePassword(insurePassword_et.getText().toString().trim())){
                        Toast.makeText(ChangePasswordActivity.this,"密码修改成功",Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(ChangePasswordActivity.this,"密码修改失败",Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });
    }
    //修改密码
    private boolean changePassword(String password){
        boolean changed=false;
        try {
            AccountManager.getInstance(Connect.getXMPPTCPConnection()).changePassword(password);
            changed=true;
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
            changed=false;
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
            changed=false;
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
            changed=false;
        }
        return changed;
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
