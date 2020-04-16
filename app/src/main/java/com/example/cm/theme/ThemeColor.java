package com.example.cm.theme;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.widget.Toolbar;



public class ThemeColor {
    public static boolean changed;
    public static String backColorStr;
    public final static String defaultBackColorStr="#FFC0CB";//默认的背景颜色字符串 少女粉
    private String colorStr;
    private String colorName;

    public String getColorName() {
        return colorName;
    }

    public String getColorStr() {
        return colorStr;
    }

    public void setColorName(String colorName) {
        this.colorName = colorName;
    }

    public void setColorStr(String colorStr) {
        this.colorStr = colorStr;
    }
    public static void setTheme(Activity activity,Toolbar toolbar){ //设置状态栏和背景栏颜色
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.getWindow().setStatusBarColor(Color.parseColor(ThemeColor.backColorStr));//设置状态栏背景
        }
        toolbar.setBackgroundColor(Color.parseColor(ThemeColor.backColorStr));
    }
}
