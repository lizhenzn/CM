package com.example.cm.myInfo;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.text.method.TransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.cm.R;

public class LoginActivity extends Activity implements View.OnClickListener{
    private Button loginBtn,pswEnBtn;
    private ImageView headIM_login;
    private EditText userET,pswET;
    private TextView registerTV;
    private Spinner spinner;
    private final int REGISTER=1;
    private  boolean VISIABLE;

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
    }

    @Override
    public void onClick(View v) {
        Intent intent=null;
        switch(v.getId()){
            case R.id.login:{
                intent=new Intent();
                intent.putExtra("zhanghao",String.valueOf(userET.getText()));
                setResult(RESULT_OK,intent);
                //页面切换动画
                overridePendingTransition(R.anim.out_from_left,R.anim.in_from_right);
                this.finish();
            }break;
            case R.id.register:{
                intent=new Intent(this,RegisterActivity.class);
                startActivityForResult(intent, REGISTER);
                overridePendingTransition(R.anim.out_from_left,R.anim.in_from_right);
            }break;
            case R.id.invisible:{
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

    }
}
