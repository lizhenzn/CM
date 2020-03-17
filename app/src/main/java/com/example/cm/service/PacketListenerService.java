package com.example.cm.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.example.cm.util.Connect;

public class PacketListenerService extends Service {
    private  MyBinder binder=new MyBinder();
    public PacketListenerService() {
       // binder=new MyBinder();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("服务创建", "onCreate: ");
        
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("服务销毁", "onDestroy: ");
    }
    public class MyBinder extends Binder{
        public void startListerer(){
            Log.d("包监听开始", "startListerer: ");
            Connect.addFriendListener();
            Connect.initChatMessageListener();
        }
  }


}
