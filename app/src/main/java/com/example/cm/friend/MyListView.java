package com.example.cm.friend;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ListView;

public class MyListView extends ListView {
    int flag=0;
    float StartX,StartY;
    public MyListView(Context context) {
        super(context);
    }
    public MyListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
    }

    public MyListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        onTouchEvent(ev);
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            StartX = ev.getX();
            StartY = ev.getY();
            return false;
        }
        if (ev.getAction() == MotionEvent.ACTION_MOVE) {
            float ScollX = ev.getX() - StartX;
            float ScollY = ev.getY() - StartY;
            // 判断是横滑还是竖滑，竖滑的话拦截move事件和up事件（不拦截会由于listview和scrollview同时执行滑动卡顿）
            if (Math.abs(ScollX) < Math.abs(ScollY)) {
                flag = 1;
                return true;
            }
            return false;
        }
        if (ev.getAction() == MotionEvent.ACTION_UP) {
            if (flag == 1) {

                return true;
            }
            return false;
        }
        return super.onInterceptTouchEvent(ev);
    }
}
