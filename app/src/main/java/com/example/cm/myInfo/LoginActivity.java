package com.example.cm.myInfo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.text.method.TransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.app.ActionBar;
import com.example.cm.R;
import com.example.cm.util.AlbumUtil;
import com.example.cm.util.Connect;
import com.example.cm.util.DataBase;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.packet.Message;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends Activity implements View.OnClickListener{
    private Button loginBtn,pswEnBtn;
    private ImageView headIM_login;
    private EditText userET,pswET;
    private TextView registerTV;
    private Spinner spinner;
    private final int REGISTER=1;
    private  boolean VISIABLE;
    private ProgressDialog progressDialog;
    private ArrayAdapter<CharSequence> adapterXML;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        init();

    }
    //初始化控件
    public void init(){
        VISIABLE=false;
        loginBtn=(Button)findViewById(R.id.login);
        userET=(EditText)findViewById(R.id.user_login);
        pswET=(EditText)findViewById(R.id.password);
        registerTV=(TextView)findViewById(R.id.register);
        spinner=(Spinner)findViewById(R.id.spinner);
        headIM_login=(ImageView)findViewById(R.id.head_login);
        pswEnBtn=(Button)findViewById(R.id.visible);

        progressDialog=new ProgressDialog(LoginActivity.this);
        //注册事件
        loginBtn.setOnClickListener(this);
        pswEnBtn.setOnClickListener(this);
        registerTV.setOnClickListener(this);
        userET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        pswET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        adapterXML= (ArrayAdapter<CharSequence>) ArrayAdapter.createFromResource(this,R.array.datalist,R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(adapterXML);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.login:{
                Log.d("点击LOGIN", "点击点击点击点击登录");
                String user = String.valueOf(userET.getText());
                Log.d("输入的登录名", "onClick: userName"+user);
                File file=new File(AlbumUtil.FRIENDHEADFILEROAD+"/"+user);
                if(!file.exists()){
                    file.mkdirs();
                }
                //TODO 判断是不是和上次登录用户一样 从而决定是否更换从数据库得到的联系人列表和聊天信息列表
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                    LoginActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.setTitle("");
                            progressDialog.setMessage("登陆中...");
                            progressDialog.setCancelable(false);
                            progressDialog.show();
                        }
                    });
                        //Connect.init();   //在程序打开之时同时初始化适配器列表，不然会有bug
                        //Connect connect = new Connect(LoginActivity.this);

                        String passwd = String.valueOf(pswET.getText());
                        if (Connect.getXMPPTCPConnection()!=null) {
                            boolean isLogined = Connect.login(user, passwd);
                            //登陆成功
                            if (isLogined) {
                                Connect.isLogined=true;
                                //TODO    其他的用户信息
                                //Connect.getUserImage(Connect.xmpptcpConnection.getUser().split("/")[0]);
                               // Log.d("", "run: 登陆成功获取头像");
                                LoginActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressDialog.dismiss();
                                        Toast.makeText(LoginActivity.this, "登陆成功", Toast.LENGTH_SHORT).show();

                                    }
                                });
                                Intent intent = new Intent();
                                intent.putExtra("zhanghao", String.valueOf(userET.getText()));
                                setResult(RESULT_OK, intent);
                                //页面切换动画
                                overridePendingTransition(R.anim.out_from_left, R.anim.in_from_right);

                                LoginActivity.this.finish();
                            } else {//登陆失败
                                progressDialog.dismiss();
                                LoginActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(LoginActivity.this, "登陆异常", Toast.LENGTH_SHORT).show();

                                    }
                                });

                                // Intent intent=new Intent();
                                // setResult(RESULT_CANCELED,intent);
                            }
                        }
                    }

                }).start();



            }break;
            case R.id.register:{
                Intent intent=new Intent(this,RegisterActivity.class);
                startActivityForResult(intent, REGISTER);
                overridePendingTransition(R.anim.out_from_left,R.anim.in_from_right);
            }break;
            case R.id.visible:{
                if(VISIABLE){
                    //设置可见
                    pswET.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    VISIABLE=false;
                }else{
                    //设置不可见
                    pswET.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    VISIABLE=true;
                }
                //设置光标位置位于最后
                pswET.setSelection(pswET.getText().length());

            }break;
            default:break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Intent intent=null;
        switch (requestCode){
            case REGISTER:{
                if(resultCode==RESULT_OK){
                userET.setText(data.getStringExtra("zhanghao"));
                }
            }break;
            default:break;
        }

    }
}
