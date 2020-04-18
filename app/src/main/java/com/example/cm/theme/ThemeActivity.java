package com.example.cm.theme;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ListView;

import com.example.cm.R;

import java.util.ArrayList;
import java.util.List;

public class ThemeActivity extends AppCompatActivity {
private List<ThemeColor> themeColors;
private ListView listView;
private ThemeAdapter adapter;
private     Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theme);
        toolbar=(Toolbar)findViewById(R.id.theme_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }
        ThemeColor.setTheme(ThemeActivity.this,toolbar);
        init();
    }
    public void init(){
        listView=findViewById(R.id.theme_lv);
        themeColors=new ArrayList<>();
        String[] colorStrs=this.getResources().getStringArray(R.array.colorStr);
        String[] colorNames=this.getResources().getStringArray(R.array.colorName);
        for(int i=0;i<colorStrs.length;i++){
            ThemeColor themeColor=new ThemeColor();
            themeColor.setColorStr(colorStrs[i]);
            themeColor.setColorName(colorNames[i]);
            themeColors.add(themeColor);
        }
        adapter=new ThemeAdapter(this,toolbar,themeColors);
        listView.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{
                this.finish();
            }break;
            default:break;
        }
        return true;
    }
}
