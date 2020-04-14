package com.example.cm.util;

import android.text.format.DateUtils;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;


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
    Date date=new Date(time);
    if(DateUtils.isToday(time)){
        date.getHours();
        simpleDateFormat=new SimpleDateFormat("HH:mm");
    }else if(isYesterday(time)){
        simpleDateFormat=new SimpleDateFormat("昨天 HH:mm");
    }else{
        simpleDateFormat=new SimpleDateFormat("MM-dd HH:mm");
    }
    dateStr=simpleDateFormat.format(date);
    return dateStr;
}
/*
* @param time
* @return boolean
*/
public  static boolean isYesterday(long time){
    boolean isYesterday=false;
    time=time+24*60*60*1000;//加一天如果判断是今天，则输入是昨天
    if(DateUtils.isToday(time)){
        isYesterday=true;
    }

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
    if(lastMesTime!=0){
        rate=(currentMesTime-3*60*1000)/lastMesTime;
    }
    if(lastMesTime==0L){
        isSurpass=true;
    }else if(rate>=1L){
        isSurpass=true;
    }
    return isSurpass;
}
}
