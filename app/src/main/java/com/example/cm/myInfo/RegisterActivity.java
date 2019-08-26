package com.example.cm.myInfo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.cm.R;

public class RegisterActivity extends AppCompatActivity {
    private EditText user_et,passwd_et,insurePasswd_et,email_et;
    private TextView birthday_tv1,birthday_tv2,birthday_tv3,sex_tv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        init();
    }

    private void init() {
        user_et=(EditText)findViewById(R.id.register_user_et);
        passwd_et=(EditText)findViewById(R.id.register_passwd_et);
        insurePasswd_et=(EditText)findViewById(R.id.register_insurePasswd_et);
        email_et=(EditText)findViewById(R.id.register_email_et_);
        birthday_tv1=(TextView)findViewById(R.id.register_birthday_tv1);
        birthday_tv2=(TextView)findViewById(R.id.register_birthday_tv2);
        birthday_tv3=(TextView)findViewById(R.id.register_birthday_tv3);
        sex_tv=(TextView)findViewById(R.id.register_sex_tv);
    }
    //身高选择
    private void birthdayChoose(){
        View contentView= View.inflate(RegisterActivity.this,R.layout.numberpicker_birthday,null);
        View rootView=View.inflate(RegisterActivity.this,R.layout.register,null);
        final NumberPicker numberPicker_year=(NumberPicker)contentView.findViewById(R.id.number_picker_birthdayYear);
        final NumberPicker numberPicker_month=(NumberPicker)contentView.findViewById(R.id.number_picker_birthdayMonth);
        final NumberPicker numberPicker_day=(NumberPicker)contentView.findViewById(R.id.number_picker_birthdayDay);

        /*numberPicker.setMaxValue(200 );
        numberPicker.setMinValue(0);
        numberPicker.setValue(175);*/
        numberPicker_year.setMaxValue(2019);
        numberPicker_year.setMinValue(1990);
        numberPicker_year.setValue(2019);
        numberPicker_month.setMaxValue(12);
        numberPicker_month.setMinValue(1);
        numberPicker_month.setValue(1);
        numberPicker_day.setMaxValue(30);
        numberPicker_day.setMinValue(1);
        numberPicker_day.setValue(1);
        PopupWindow popupWindow=new PopupWindow(contentView, LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT,true);
        popupWindow.showAtLocation(rootView, Gravity.BOTTOM,0,0);
        numberPicker_year.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                

            }
        });


    }
    //性别选择
    private void sexChoose(){

    }
}
