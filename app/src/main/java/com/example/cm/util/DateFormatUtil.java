package com.example.cm.util;

import android.text.format.DateUtils;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

import static android.text.format.DateUtils.isToday;

public class DateFormatUtil {
/*
* 得到对应的时间字符串
* @
* @param time
* @return dateStr
* */
public  static  String getDateStr(long time){
    String dateStr=null;
    SimpleDateFormat simpleDateFormat=null;
    Date currentDate=new Date();
    Date date=new Date(time);
    if(DateUtils.isToday(time)){
        simpleDateFormat=new SimpleDateFormat("今天 HH:mm");
    }else if(isYesterday(time)){
        simpleDateFormat=new SimpleDateFormat("昨天 HH:mm");
    }else{
        simpleDateFormat=new SimpleDateFormat("MM.dd HH:mm");
    }
    dateStr=simpleDateFormat.format(date);
    return dateStr;
}
/*
* @param time
* @return boolean
*/
public  static boolean isYesterday(long timr){
    boolean isYesterday=false;

    return isYesterday;
}
/*
* 判断遇上一条信息时间间隔是否超过两分钟
* @param lastMesTime
* @param currentMesTime
* @return boolean
* */
public static boolean surpassTwoMinutes(long lastMesTime,long currentMesTime){
    boolean isSurpass=false;
    long rate= (long) 0.5;
    Log.e("", "surpassTwoMinutes: "+lastMesTime );
    Log.e("", "surpassTwoMinutes: "+currentMesTime );
    if(lastMesTime!=0){
        rate=(currentMesTime-3*60*1000)/lastMesTime;
    }
    Log.e("", "surpassTwoMinutes: "+rate );
    if(lastMesTime==0L){
        isSurpass=true;
    }else if(rate>=1L){
        Log.e("", "surpassTwoMinutes: "+(currentMesTime-2*60*1000)/lastMesTime );
        isSurpass=true;
    }
    return isSurpass;
}
}
