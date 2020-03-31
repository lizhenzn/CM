package com.example.cm.myInfo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cm.R;
import com.example.cm.util.AlbumUtil;
import com.example.cm.util.Connect;
import com.example.cm.util.MessageManager;

import java.io.File;

public class LoginActivity extends Activity implements View.OnClickListener{
    private Button loginBtn,pswEnBtn;
    private ImageView headIM_login;
    private EditText userET,pswET;
    private TextView registerTV;
    private Spinner spinner;
    private int loginResult;
    private String result;
    private final int REGISTER=1;
    private  boolean VISIABLE;
    private ProgressDialog progressDialog;
    private String cacheUser,cachePasswd;
    private ArrayAdapter<CharSequence> adapterXML;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        init();
        android.app.ActionBar actionBar=getActionBar();
        if(actionBar!=null)actionBar.setDisplayShowTitleEnabled(false);
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
        cacheUser=MessageManager.getSharedPreferences().getString("userName","");
        cachePasswd= MessageManager.getSharedPreferences().getString("passward","");//缓存的密码

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
        if(!cacheUser.equals("")) {//登陆过，有缓存帐号和密码
            userET.setText(cacheUser);
            pswET.setText(cachePasswd);
        }else{
            userET.setHint("帐号");
            pswET.setHint("密码");
        }
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
                            //@return 0:服务器连接异常 1:登陆异常 2：登陆成功 3：密码错误
                            loginResult=1;
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                loginResult = Connect.login(user, passwd);
                            }
                            //登陆成功
                            if (loginResult==2) {
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
                                LoginActivity.this.finish();
                                overridePendingTransition(R.anim.out_from_left, R.anim.in_from_right);
                            } else {//登陆失败
                                progressDialog.dismiss();
                                if(loginResult==0){
                                    result="服务器连接异常";
                                }else if(loginResult==3){
                                    result="账户或密码错误";
                                }else {
                                    result="登陆异常";
                                }
                                LoginActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(LoginActivity.this, result, Toast.LENGTH_SHORT).show();
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
                if(!VISIABLE){
                    //设置可见
                    pswET.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    VISIABLE=true;
                }else{
                    //设置不可见
                    pswET.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    VISIABLE=false;
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
