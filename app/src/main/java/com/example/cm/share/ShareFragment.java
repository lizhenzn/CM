package com.example.cm.share;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import org.jivesoftware.smackx.iqregister.AccountManager;


import com.example.cm.ConnectService;
import com.example.cm.R;
import com.example.cm.myInfo.LoginActivity;
import com.example.cm.myInfo.MyInfoActivity;
import com.example.cm.util.Connect;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;

import java.io.IOException;

import main.CallBackMethods;
import main.TransferManager;
import main.UserInfo;

import static com.example.cm.MainActivity.setToolbarText;

public class ShareFragment extends Fragment implements View.OnClickListener {
    private Context context;
    private View view;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setToolbarText("广场");

    }

    @Override
    public void onResume() {
        super.onResume();
        setToolbarText("广场");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        context=getActivity();
        view=View.inflate(context, R.layout.share,null);
        Button button=(Button)view.findViewById(R.id.share_btn);
        Button login=(Button)view.findViewById(R.id.login_share);
        Button disconnect=(Button)view.findViewById(R.id.disConnect);
        button.setOnClickListener(this);
        login.setOnClickListener(this);
        disconnect.setOnClickListener(this);


       /* button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*UserInfo user=new UserInfo();
                user.setUserName("lizhen");
                user.setEmail("229010ad655");
                user.setPassWord("123456");
                TransferManager transferManager=new TransferManager();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        transferManager.doRegister(user, new CallBackMethods() {
                            @Override
                            public void onSuccess() {
                                Log.d("doRegister","666");
                            }

                            @Override
                            public void onError() {

                            }

                            @Override
                            public void onFailed() {

                            }

                            @Override
                            public void onBadLink() {

                            }
                        });
                    }
                }).start();*/
              /*  Connect connect=new Connect(getActivity());
                XMPPTCPConnection xmpptcpConnection=connect.getXmppConnection();
                if(xmpptcpConnection!=null)
                    Toast.makeText(context,"Connect Openfile Success!", Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(context,"Connect Openfile Fail!", Toast.LENGTH_LONG).show();




                //if(connectService.login("lizhen","zn521128"))
                    //Log.d("loginOpenfile","Success");
               //boolean b= connectService.login("admin","zn521128");
                 Log.d("login","loginOpenfile");
                Log.d("login000001", "onClick: ");
               // ConnectService.getConnection();
                //ConnectService.login("lizhen","zn521128");
            }
        });*/

        return  view;
    }

    @Override
    public void onClick(View v) {
        Connect connect=new Connect(getActivity());

        switch(v.getId()){
            case R.id.share_btn:{
            }break;
            case R.id.login_share:{
            }break;
            case R.id.disConnect:{
            }break;
            default:break;
        }
    }
}
